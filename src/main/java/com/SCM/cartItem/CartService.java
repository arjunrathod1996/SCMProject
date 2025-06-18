package com.SCM.cartItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.SCM.cartItem.CartItem.CartItemStatus;
import com.SCM.chartandFile.ExcelReader;
import com.SCM.chartandFile.PdfUtils;
import com.SCM.controllers.BusinessController;
import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.Customer;
import com.SCM.entities.User;
import com.SCM.photo.PhotoMerchant;
import com.SCM.photo.PhotoMerchantRepository;
import com.SCM.repository.CustomerRepository;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.UserRepository;
import com.SCM.role.Role.RoleType;
import com.SCM.service.CommonService;
import com.SCM.utils.Utils;

import jakarta.servlet.ServletOutputStream;
import jakarta.transaction.Transactional;

@Service
public class CartService {

	@Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private PhotoMerchantRepository photoMerchantRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    CustomerRepository customerRepository;
    
    @Autowired
    CommonService commonService;

    @Autowired
    BillingSummaryRepository billingSummaryRepository;
    
    @Autowired
    MerchantRepository merchantRepository;
    
    @Autowired
	PdfUtils pdfUtils;
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);
    
    private static final String INVOICE_ID_FORMAT = "%s/%d-%d";

	private static final String RECEIPT_ID_FORMAT = "%s"+"/"+"%d-%d";
	private static final String FILE_NAME_FORMAT = "%s - %s-%s.pdf";
	private static final String CREDIT_NOTE_ID_FORMAT = "CN" + "/" + "%d-%d";
   
  @Transactional
  public void addToCart(Long userId, Long photoMerchantId, int quantity) {
      try {
          // Fetch the user, customer, and photo merchant
          User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
          
         
          
         /* Customer customer = customerRepository.findByEmail(commonService.getLoggedInUser().getEmail())
                  .orElseThrow(() -> new RuntimeException("Customer not found"));*/ 
          
          
          User loggedInUser = commonService.getLoggedInUser();
	        Customer customer = null;

	        if (loggedInUser != null) {
	            if (loggedInUser.getPhoneNumber() != null) {
	                // If the user is logged in with a phone number
	                List<Customer> customers = customerRepository.findByMobileNumber(loggedInUser.getPhoneNumber());
	                if (!customers.isEmpty()) {
	                    // Assuming there is only one customer per phone number
	                    customer = customers.get(0);
	                }
	            } else if (loggedInUser.getEmail() != null) {
	                // If the user is logged in with an email
//	                List<Customer> customers = customerRepository.findByEmail(user.getEmail());
//	                if (!customers.isEmpty()) {
//	                    // Assuming there is only one customer per email
//	                    customer = customers.get(0);
//	                }
	            	customer = customerRepository.findByEmail(loggedInUser.getEmail()).orElse(null);
	            }
	        }
          
         
          
          PhotoMerchant photoMerchant = photoMerchantRepository.findById(photoMerchantId)
                  .orElseThrow(() -> new RuntimeException("PhotoMerchant not found"));

          // Find the existing cart items for the customer and photo merchant with status COMPLETED
          List<CartItem> completedCartItems = cartItemRepository.findByCustomerAndPhotoMerchantAndStatus(customer, photoMerchant, CartItemStatus.COMPLETED);
         
          // Find the existing cart item with status PENDING
          List<CartItem> pendingCartItems = cartItemRepository.findByCustomerAndPhotoMerchantAndStatus(customer, photoMerchant, CartItemStatus.PENDING);

          CartItem cartItem;
	        if (!pendingCartItems.isEmpty()) {
	        	cartItem = pendingCartItems.get(0);
	        //cartItem.setQuantity(cartItem.getQuantity() + quantity);
	        int previousQuantity = cartItem.getQuantity();
	        if (quantity > previousQuantity) {
	            cartItem.setQuantity(previousQuantity + (quantity - previousQuantity));
	        } else if (quantity < previousQuantity) {
	            cartItem.setQuantity(previousQuantity - (previousQuantity - quantity));
	        }
	        System.out.println("Existing cart item updated: " + cartItem.getQuantity());
	    } else {
	        cartItem = new CartItem();
	        cartItem.setCustomer(customer);
	        cartItem.setPhotoMerchant(photoMerchant);
	        cartItem.setQuantity(quantity);
	        String priceString = photoMerchant.getPhotoExtras().getPrice();
	        if (priceString == null) {
	            throw new RuntimeException("PhotoMerchant price is null");
	        }
	        cartItem.setPrice(Double.parseDouble(priceString.trim())); // Ensure price is not null or empty
	        cartItem.setStatus(CartItem.CartItemStatus.PENDING); // Set initial status to PENDING
	        System.out.println("New cart item created with price: " + cartItem.getPrice());
	    }
	    cartItem.setAmount(cartItem.getQuantity() * cartItem.getPrice());
	    cartItemRepository.save(cartItem);
	    System.out.println("Cart item saved: " + cartItem.getId());
          
      } catch (NumberFormatException e) {
          System.err.println("Error occurred while parsing price: " + e.getMessage());
          throw new RuntimeException("Error occurred while adding to cart: invalid price format", e);
      } catch (Exception e) {
          System.err.println("Error occurred while adding to cart: " + e.getMessage());
          throw new RuntimeException("Error occurred while adding to cart", e);
      }
  }

    public Integer getTotalQuantityByUserIdAndMerchantIdAndCustomerId(
            CartItem.CartItemStatus status, Long userId, Long merchantId, Long customerId) {
        return cartItemRepository.getTotalQuantityByUserIdAndMerchantIdAndCustomerId(status, userId, merchantId, customerId);
    }
    
    public Integer getTotalAmountByUserIdAndMerchantIdAndCustomerId(
            CartItem.CartItemStatus status, Long userId, Long merchantId, Long customerId) {
        return cartItemRepository.getTotalAmountByUserIdAndMerchantIdAndCustomerId(status, userId, merchantId, customerId);
    }

    public List<CartItem> getCartItemsByMultipleStatuses(Long userId, Long merchantId, Long customerId, Long photoId) {
        List<CartItemStatus> statuses = Arrays.asList(CartItemStatus.PENDING, CartItemStatus.COMPLETED);
        return cartItemRepository.findByStatusesAndUserIdAndMerchantIdAndCustomerIdAndPhotoMerchantId(
                statuses, userId, merchantId, customerId, photoId);
    }

    public Page<CartItem> getBillSummary(Pageable pageable, User user, Long merchantID,String email,String mobileNumber,String startDate,String endDate ,CartItemStatus status) {

        if (user == null) {
            throw new RuntimeException("Not Allowed");
        }
        
        

        Sort sort = pageable.getSort();
        Business business = null;
        Merchant merchant = null;
        List<Merchant> merchants = null;
        Page<CartItem> bill = null;
		
		if(user.getRole().getName() == RoleType.ROLE_MERCHANT_ADMIN) {
			business = user.getBusiness();
		}
		
		if(user.getRole().getName() == RoleType.ROLE_ADMIN) {
			business = user.getMerchant().getBusiness();
		}

        if (sort == null) {
            sort = Sort.by(Sort.Direction.DESC, "creationTime");
        }

        if (endDate != null && startDate == null)
            startDate = "1970-01-01";
        if (startDate != null && endDate == null)
            endDate = Utils.dateToString(Utils.now(), "yyyy-MM-dd");
        
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

   

        bill = cartItemRepository.findCartItemPageWise(pageRequest,user.getBusiness().getId(),merchantID,email,mobileNumber,startDate,endDate,status);
        
       for(CartItem b : bill) {
    	   
    	   System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>> : " + b.getId());
    	   
       }

        return bill;
    }
    
    
    public Page<Customer> getCustomer(Pageable pageable, User user, Long merchantID,String email,String mobileNumber,String startDate,String endDate ,CartItemStatus status) {

        if (user == null) {
            throw new RuntimeException("Not Allowed");
        }
        
        Sort sort = pageable.getSort();
        Business business = null;
        Merchant merchant = null;
        List<Merchant> merchants = null;
        Page<Customer> bill = null;
		
		if(user.getRole().getName() == RoleType.ROLE_MERCHANT_ADMIN) {
			business = user.getBusiness();
		}
		
		if(user.getRole().getName() == RoleType.ROLE_MERCHANT_STAFF) {
			business = user.getMerchant().getBusiness();
		}
		
        if (sort == null) {
            sort = Sort.by(Sort.Direction.DESC, "creationTime");
        }

        if (endDate != null && startDate == null)
            startDate = "1970-01-01";
        if (startDate != null && endDate == null)
            endDate = Utils.dateToString(Utils.now(), "yyyy-MM-dd");
        
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        
        System.out.println(" business id : " + business.getId());
        System.out.println((" user id : " + user.getId()));
        System.out.println(" merchant id : " + user.getMerchant().getId());
   

        bill = customerRepository.findCustomersByUserIdAndMerchantId(user.getId(), user.getMerchant().getId(), pageRequest);

        return bill;
    }

    public CartItem saveCart(CartItem cartItem) {
        try {
            Long cartItemId = cartItem.getId();
            CartItem existingCartItem = cartItemRepository.findById(cartItemId).orElse(null);

            if (existingCartItem != null) {
                logger.info("CartItem found with ID {}. Updating its fields.", cartItemId);
                existingCartItem.setStatus(CartItemStatus.CANCELED);
                existingCartItem.setDeleted(true);
                return cartItemRepository.save(existingCartItem);
            } else {
                logger.error("CartItem with ID {} not found for update.", cartItemId);
                return null; // or handle it according to your application logic
            }
        } catch (Exception ex) {
            logger.error("An error occurred while saving the cart item: {}", ex.getMessage(), ex);
            return null;
        }
    }
    
    
public byte[] exportInvoice(CartItem invoice) {
		
		byte[] buf = null;
		HashMap<String, Object> attributes = new HashMap<>();
		//InvoiceContent content = invoice.getInvoiceExtras().getContent();
		
		Double total = 0.0;

		total = invoice.getAmount();
		attributes.put("total", Utils.twoDecimalValue(total.floatValue()));

		//attributes.put("content",content);
		attributes.put("invoice", invoice);
		
		
		
		try {
			
			buf = pdfUtils.generatePDF("user/invoice_template", attributes);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return buf;
	}

public String getInvoiceTitle(CartItem invoice) {
    return String.format(FILE_NAME_FORMAT, invoice.getPhotoMerchant().getMerchant().getBusiness().getName(),invoice.getId(),invoice.getCreationTime());
}

//public void exportBillSummary(ServletOutputStream outputStream, Pageable pageable, User user, Long merchantID,
//        String name, String email, String mobileNumber, CartItemStatus status, String startDate, String endDate) {
//		try {
//		Date start = new Date();
//		
//		String[] columns = new String[]{
//		"customer Name",
//		"Mobile Number",
//		"Status",
//		"Amount",
//		"creation Time"
//		};
//		
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		
//		Page<CartItem> activity = getBillSummary(pageable, user, merchantID, email, mobileNumber, startDate, endDate, status);
//		
//		List<String[]> rowData = new ArrayList<>();
//		
//		for (CartItem a : activity) {
//		String[] values = new String[columns.length];
//		
//		String firstName = a.getCustomer().getFirstName();
//		String mobileNumber_ = a.getCustomer().getMobileNumber();
//		CartItemStatus status_ = a.getStatus();
//		Double amount_ = a.getAmount();
//		Date creationTime = a.getCreationTime();
//		String creationTimeString = dateFormat.format(creationTime);
//		
//		values[0] = firstName;
//		values[1] = mobileNumber_;
//		values[2] = status_.toString(); // Convert enum to String
//		values[3] = String.valueOf(amount_); // Convert Double to String
//		values[4] = creationTimeString;
//		rowData.add(values);
//		}
//		
//			new ExcelReader().generateExcel(columns, rowData, outputStream);
//		} catch (Exception ex) {
//			logger.error("Error exporting bill summary: {}", ex.getMessage(), ex);
//		}
//	}

public void exportBillSummary(ServletOutputStream outputStream, Pageable pageable, User user, Long merchantID,
        String name, String email, String mobileNumber, CartItemStatus status, String startDate, String endDate, boolean usePagination) {
    try {
        Date start = new Date();

        String[] columns = new String[]{
            "customer Name",
            "Mobile Number",
            "Status",
            "Amount",
            "creation Time"
        };

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<CartItem> activity;
        if (usePagination) {
            activity = getBillSummary(pageable, user, merchantID, email, mobileNumber, startDate, endDate, status).getContent();
        } else {
            activity = getBillSummaryWithoutPagination(user, merchantID, email, mobileNumber, startDate, endDate, status);
        }

        List<String[]> rowData = new ArrayList<>();

        for (CartItem a : activity) {
            String[] values = new String[columns.length];

            String firstName = a.getCustomer().getFirstName();
            String mobileNumber_ = a.getCustomer().getMobileNumber();
            CartItemStatus status_ = a.getStatus();
            Double amount_ = a.getAmount();
            Date creationTime = a.getCreationTime();
            String creationTimeString = dateFormat.format(creationTime);

            values[0] = firstName;
            values[1] = mobileNumber_;
            values[2] = status_.toString(); // Convert enum to String
            values[3] = String.valueOf(amount_); // Convert Double to String
            values[4] = creationTimeString;
            rowData.add(values);
        }

        new ExcelReader().generateExcel(columns, rowData, outputStream);
    } catch (Exception ex) {
        logger.error("Error exporting bill summary: {}", ex.getMessage(), ex);
    }
}

public List<CartItem> getBillSummaryWithoutPagination(User user, Long merchantID, String email, String mobileNumber, String startDate, String endDate, CartItemStatus status) {
    if (user == null) {
        throw new RuntimeException("Not Allowed");
    }

    Sort sort = Sort.by(Sort.Direction.DESC, "creationTime");

    if (endDate != null && startDate == null) {
        startDate = "1970-01-01";
    }
    if (startDate != null && endDate == null) {
        endDate = Utils.dateToString(Utils.now(), "yyyy-MM-dd");
    }

    return cartItemRepository.findCartItemsWithoutPagination(user.getBusiness().getId(), merchantID, email, mobileNumber, startDate, endDate, status, sort);
}

	
}


