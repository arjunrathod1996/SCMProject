package com.SCM.photo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.domain.JpaSort.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.photo.Photo.PhotoType;
import com.SCM.repository.BusinessRepository;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.UserRepository;
import com.SCM.service.CommonService;
import com.SCM.utils.Utils;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@RequestMapping("/api/gallery")
@RestController
public class GalleryController {
	
	@Value("${com.crm.config.env}")
	private String env;
	
	@Autowired
	GalleryService galleryService;
	
	@Autowired
	BusinessRepository businessRepository;
	
	@Autowired
	MerchantRepository merchantRepository;
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	PhotoRepository photoRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PhotoMerchantRepository photoMerchantRepository;
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	 private final String PHOTO_EXTENSION = "jpg";
	 private final String PHOTO_CONTENT_TYPE = "image/jpeg";
	
	@RequestMapping(value = "/add_photo", method=RequestMethod.POST)
	public void addPhoto(HttpServletResponse response,
			HttpServletRequest request,
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "businessID", required = false) Long businessID,
			@RequestParam(value = "merchantID", required = false)Long merchantID,
			@RequestParam(value = "type") PhotoType type,
			@RequestParam(value = "sequence",required = false) Long sequence) throws Exception {
		
		
		
		if(businessID == null && merchantID == null) {
			throw new RuntimeException("Missing Business/Merchant");
		}
		
		galleryService.authorize();
		
		User user = commonService.getLoggedInUser();
		
		if(user.getBusiness() != null) {
			businessID = user.getBusiness().getId();
		}
		
		String fileName = file.getOriginalFilename();
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
		String contentType = Files.probeContentType(new File(fileName).toPath());
		byte[] content = file.getBytes();
		int size = content.length;
		
		Photo photo = new Photo();
		
		photo.setType(type);
		photo.setSequence(sequence);
		photo.setContentType(contentType);
		photo.setSize(size);
		
		if(merchantID != null) {
			Merchant merchant = merchantRepository.findById(merchantID).get();
			photo.setMerchant(merchant);
			photo.setBusiness(merchant.getBusiness());
		}else {
			photo.setBusiness(businessRepository.findById(businessID).get());
		}
		
		try {
			photo = galleryService.addPhoto(photo, extension, content);
		}catch (Exception e) {
			logger.error("Failed to Add/Replace Photo..." + e.getMessage());
			e.printStackTrace();
		}
		
		response.setContentType("application/json");
		
		if(photo != null) {
			String responseData = "{\"fileName\":\""+photo.getFileName() + "\", \"endPoint\"}";
			response.getWriter().print(responseData);
		}else
			response.getWriter().print("{}");
	}
	

	@Deprecated
	@RequestMapping(value = "/photos", method = RequestMethod.GET)
	public ResponseEntity<?> getPhotos(@RequestParam(value = "businessID") Long businessID,
			@RequestParam(value = "merchantID", required = false) Long merchantID) throws Exception {
		
		if(businessID == null && merchantID == null) {
			throw new RuntimeException("Missing Business/Merchant");
		}
		
		galleryService.authorize();
		
		User user = commonService.getLoggedInUser();
		
		if(user.getBusiness() != null) {
			businessID = user.getBusiness().getId();
		}
		
		java.util.List<Photo> photos = null;
		
		if(merchantID != null) {
			Merchant merchant = merchantRepository.findById(merchantID).get();
			photos = photoRepository.findByMerchantAndDeletedOrderBySequence(merchant, false);		
		}else {
			Business business = businessRepository.findById(businessID).get();
			photos = photoRepository.findByBusinessID(business.getId());
		}
		
		return new ResponseEntity<List<Photo>>(photos, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/update_extras", method=RequestMethod.GET)
	public ResponseEntity<?> updateExtras(@RequestParam(value = "businessID",required = false) Long businessID,
			@RequestParam(value = "merchantID",required = false) Long merchantID,
			@RequestParam(value = "sequence") Long sequence,
			@RequestBody PhotoExtras content)	{
		

		if(businessID == null && merchantID == null) {
			throw new RuntimeException("Missing Business/Merchant");
		}
		
		galleryService.authorize();
		
		
		User user = commonService.getLoggedInUser();
		
		Business business = null;
		
		boolean status = false;
		
		if(merchantID != null) {
			Merchant merchant = merchantRepository.findById(merchantID).get();
			status = galleryService.updatePhotoExtras(merchant, sequence, content);
		}else {
			if(user.getBusiness() != null)
				businessID = user.getBusiness().getId();
			
			business = businessRepository.findById(businessID).get();
			status = galleryService.updatePhotoExtras(business, sequence, content);
		}
		
		if(status)
			return new ResponseEntity<String>("{}",HttpStatus.OK);
		else
			return new ResponseEntity<String>("{}",HttpStatus.BAD_REQUEST);
	}
	
	
	@RequestMapping(value = "/delete_extras", method=RequestMethod.GET)
	public void deletePhoto(HttpServletResponse response,
			@RequestParam(value = "businessID",required = false) Long businessID,
			@RequestParam(value = "merchantID",required = false) Long merchantID,
			@RequestParam(value = "type") PhotoType type,
			@RequestParam(value = "sequence") Long sequence) throws IOException, java.io.IOException {
		
		if(businessID == null && merchantID == null) {
			throw new RuntimeException("Missing Business/Merchant");
		}
		
		galleryService.authorize();
		User user = commonService.getLoggedInUser();
		
		boolean flag = false;
		
		if(merchantID != null) {
			Merchant merchant = merchantRepository.findById(merchantID).get();
			flag = galleryService.deletePhoto(merchant, type, sequence);
		}else {
			
			if(user.getBusiness() != null) {
				businessID = user.getBusiness().getId();
			}
			
			Business business = businessRepository.findById(businessID).get();
			flag = galleryService.deletePhoto(business, type, sequence);
		}
		
		response.setContentType("application/json");
		String content = "{\"status\": " + flag + "}";
		response.getWriter().print(content);
				
		
	}	
	
	
//	@RequestMapping(value = "", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseEntity<?> save(@RequestBody Photo photo, 
//                                  @RequestParam(value="id", required = false) Long id,
//                                  @RequestParam(value = "merchantID", required = false) Long merchantID,
//                                  @RequestParam(value = "businessID", required = false) Long businessID) {
//
//        try {
//            String imgData = photo.getImgDataSrc().split(",")[1]; // Extract base64 data
//
//            Base64.Decoder decoder = Base64.getDecoder();
//            byte[] content = decoder.decode(imgData);  
//
//            // Define the directory path relative to the working directory
//            String directoryPath = "src/main/resources/static/images";
//            File directory = new File(directoryPath);
//
//            // Create the directory if it doesn't exist
//            if (!directory.exists()) {
//                directory.mkdirs();
//            }
//
//            // Generate a unique file name
//            String fileName = "photo_" + System.currentTimeMillis() + ".png";
//            java.nio.file.Path path = Paths.get(directoryPath, fileName);
//
//            // Save the file
//            Files.write(path, content);
//
//            // Update Photo entity with file path
//            photo.setImgDataSrc(fileName); // Store only the file name
//            photo.setType(photo.getType());
//            photo.setContentType("image/png"); // Set the correct content type
//            photo.setSize(content.length);
//            photo.setSequence(photo.getId());
//
//            String fileName_ = generateFileName(photo,PHOTO_CONTENT_TYPE);
//			photo.setFileName(fileName_);
//			
//			photoRepository.save(photo);
//
//        } catch (Exception e) {
//            System.err.println("Error while updating profile picture: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while updating profile picture");
//        }
//
//        return ResponseEntity.ok().build();
//    }
	
	
	
/*	@RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> save(@RequestBody Photo photo, 
                                  @RequestParam(value="id", required = false) Long id,
                                  @RequestParam(value = "merchantID", required = false) Long merchantID,
                                  @RequestParam(value = "businessID", required = false) Long businessID) {

        try {
            String imgData = photo.getImgDataSrc().split(",")[1]; // Extract base64 data

            Base64.Decoder decoder = Base64.getDecoder();
            byte[] content = decoder.decode(imgData);  

            // Define the directory path relative to the working directory
            String directoryPath = "src/main/resources/static/images";
            File directory = new File(directoryPath);

            // Create the directory if it doesn't exist
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate a unique file name
            String fileName = "photo_" + System.currentTimeMillis() + ".png";
            java.nio.file.Path path = Paths.get(directoryPath, fileName);

            // Save the file
            Files.write(path, content);

            // Update Photo entity with file path
            photo.setImgDataSrc(fileName); // Store only the file name
            photo.setType(photo.getType());
            photo.setContentType("image/png"); // Set the correct content type
            photo.setSize(content.length);
            photo.setSequence(photo.getId());

            String fileName_ = generateFileName(photo,PHOTO_CONTENT_TYPE);
			photo.setFileName(fileName_);
			
			photoRepository.save(photo);

        } catch (Exception e) {
            System.err.println("Error while updating profile picture: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while updating profile picture");
        }

        return ResponseEntity.ok().build();
    }*/
	
	
	
//	@RequestMapping(value = "", method = RequestMethod.POST)
//	@ResponseBody
//	public ResponseEntity<?> save(@RequestBody Photo photo, 
//	                              @RequestParam(value="id", required = false) Long id,
//	                              @RequestParam(value = "merchantID", required = false) Long merchantID,
//	                              @RequestParam(value = "businessID", required = false) Long businessID) {
//
//	    try {
//	        String imgData = photo.getImgDataSrc().split(",")[1]; // Extract base64 data
//
//	        Base64.Decoder decoder = Base64.getDecoder();
//	        byte[] content = decoder.decode(imgData);  
//
//	        // Define the directory path relative to the working directory
//	        String directoryPath = "src/main/resources/static/images";
//	        File directory = new File(directoryPath);
//
//	        // Create the directory if it doesn't exist
//	        if (!directory.exists()) {
//	            directory.mkdirs();
//	        }
//
//	        // Generate a unique file name
//	        String fileName = "photo_" + System.currentTimeMillis() + ".png";
//	        java.nio.file.Path path = Paths.get(directoryPath, fileName);
//
//	        // Save the file
//	        Files.write(path, content);
//
//	        // Update Photo entity with file path
//	        photo.setImgDataSrc(fileName); // Store only the file name
//	        photo.setType(photo.getType());
//	        photo.setContentType("image/png"); // Set the correct content type
//	        photo.setSize(content.length);
//	        photo.setSequence(photo.getId());
//
//	        String fileName_ = generateFileName(photo, PHOTO_CONTENT_TYPE);
//	        photo.setFileName(fileName_);
//	        
//	      
//	        // Save or update the Photo entity
//	        if (id != null) {
//	            // If the ID is present, update the existing entity
//	            Photo existingPhoto = photoRepository.findById(id).orElse(null);
//	            if (existingPhoto != null) {
//	                existingPhoto.setImgDataSrc(photo.getImgDataSrc());
//	                existingPhoto.setType(photo.getType());
//	                existingPhoto.setContentType(photo.getContentType());
//	                existingPhoto.setSize(photo.getSize());
//	                existingPhoto.setSequence(photo.getSequence());
//	                existingPhoto.setFileName(photo.getFileName());
//	                existingPhoto.setPhotoExtras(photo.getPhotoExtras());
//	                photoRepository.save(existingPhoto);
//	            } else {
//	                // Handle the case where the photo with the provided ID doesn't exist
//	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Photo with ID " + photo.getId() + " not found.");
//	            }
//	        } else {
//	            // If the ID is not present, save a new entity
//	            photoRepository.save(photo);
//	        }
//
//	    } catch (Exception e) {
//	        System.err.println("Error while updating profile picture: " + e.getMessage());
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while updating profile picture");
//	    }
//	    
//	    
//	    return ResponseEntity.ok().build();
//	}
	
	
	
	@RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> save(@RequestBody Photo photo, 
                                  @RequestParam(value="id", required = false) Long id,
                                  @RequestParam(value = "merchantID", required = false) Long merchantID,
                                  @RequestParam(value = "businessID", required = false) Long businessID) {

        try {
            String imgData = photo.getImgDataSrc().split(",")[1]; // Extract base64 data

            Base64.Decoder decoder = Base64.getDecoder();
            byte[] content = decoder.decode(imgData);  

            // Define the directory path relative to the working directory
            String directoryPath = "src/main/resources/static/images";
            File directory = new File(directoryPath);

            // Create the directory if it doesn't exist
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate a unique file name
            String fileName = "photo_" + System.currentTimeMillis() + ".png";
            java.nio.file.Path path = Paths.get(directoryPath, fileName);

            // Save the file
            Files.write(path, content);

            // Update Photo entity with file path
            Business business = null;
            Merchant merchant = null;
            if(businessID != null) {
            	business = businessRepository.findById(businessID).get();
            }
            
            if(merchantID != null) {
            	merchant = merchantRepository.findById(merchantID).get();
            }
            
            photo.setImgDataSrc("/images/" + fileName); // Store the relative URL to the image
            photo.setType(photo.getType());
            photo.setContentType("image/png"); // Set the correct content type
            photo.setSize(content.length);
            photo.setSequence(photo.getId());
            photo.setBusiness(business);
            photo.setMerchant(merchant);

            // Save or update the Photo entity
            if (id != null) {
                // If the ID is present, update the existing entity
                Photo existingPhoto = photoRepository.findById(id).orElse(null);
                if (existingPhoto != null) {
                    existingPhoto.setImgDataSrc(photo.getImgDataSrc());
                    existingPhoto.setType(photo.getType());
                    existingPhoto.setContentType(photo.getContentType());
                    existingPhoto.setSize(photo.getSize());
                    existingPhoto.setSequence(photo.getSequence());
                    existingPhoto.setFileName(photo.getFileName());
                    existingPhoto.setPhotoExtras(photo.getPhotoExtras());
                    photoRepository.save(existingPhoto);
                } else {
                    // Handle the case where the photo with the provided ID doesn't exist
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Photo with ID " + id + " not found.");
                }
            } else {
                // If the ID is not present, save a new entity
                photoRepository.save(photo);
            }

            // Return the URL of the saved image
            String imageUrl = "/images/" + fileName;
           
            return ResponseEntity.ok(imageUrl);

        } catch (Exception e) {
            System.err.println("Error while updating profile picture: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while updating profile picture");
        }

	}
	
	
	@RequestMapping(value = "/my", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveMyGallery(@RequestBody PhotoMerchant photo, 
                                  @RequestParam(value="id", required = false) Long id,
                                  @RequestParam(value = "merchantID", required = false) Long merchantID,
                                  @RequestParam(value = "businessID", required = false) Long businessID,
                                  @RequestParam(value = "merchantStaffID", required = false) Long merchantStaffID) {

        try {
            String imgData = photo.getImgDataSrc().split(",")[1]; // Extract base64 data

            Base64.Decoder decoder = Base64.getDecoder();
            byte[] content = decoder.decode(imgData);  

            // Define the directory path relative to the working directory
            String directoryPath = "src/main/resources/static/images";
            File directory = new File(directoryPath);

            // Create the directory if it doesn't exist
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate a unique file name
            String fileName = "photo_" + System.currentTimeMillis() + ".png";
            java.nio.file.Path path = Paths.get(directoryPath, fileName);

            // Save the file
            Files.write(path, content);

            // Update Photo entity with file path
            User user = null;
            Merchant merchant = null;
            if(merchantStaffID != null) {
            	user = userRepository.findById(merchantStaffID).get();
            }
            
            if(merchantID != null) {
            	merchant = merchantRepository.findById(merchantID).get();
            }
            
            photo.setImgDataSrc("/images/" + fileName); // Store the relative URL to the image
            photo.setType(photo.getType());
            photo.setContentType("image/png"); // Set the correct content type
            photo.setSize(content.length);
            photo.setSequence(photo.getId());
            photo.setUser(user);
            photo.setMerchant(merchant);

            // Save or update the Photo entity
            if (id != null) {
                // If the ID is present, update the existing entity
                PhotoMerchant existingPhoto = photoMerchantRepository.findById(id).orElse(null);
                if (existingPhoto != null) {
                    existingPhoto.setImgDataSrc(photo.getImgDataSrc());
                    existingPhoto.setType(photo.getType());
                    existingPhoto.setContentType(photo.getContentType());
                    existingPhoto.setSize(photo.getSize());
                    existingPhoto.setSequence(photo.getSequence());
                    existingPhoto.setFileName(photo.getFileName());
                    existingPhoto.setPhotoExtras(photo.getPhotoExtras());
                    photoMerchantRepository.save(existingPhoto);
                } else {
                    // Handle the case where the photo with the provided ID doesn't exist
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Photo with ID " + id + " not found.");
                }
            } else {
                // If the ID is not present, save a new entity
            	photoMerchantRepository.save(photo);
            }

            // Return the URL of the saved image
            String imageUrl = "/images/" + fileName;
           
            return ResponseEntity.ok(imageUrl);

        } catch (Exception e) {
            System.err.println("Error while updating profile picture: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while updating profile picture");
        }

	}
	
	
	
	private String generateFileName(Photo photo, String extension) {
		// {env}/{tag}/{business-id}/{timestamp}
		if(env == null)
			env = "";
		env = env.toLowerCase();
		String fileName = null;
		
		if(photo.getMerchant() != null)
			fileName = String.format(Photo.OUTLET_PHOTO_FILE_NAME,
					env,
					photo.getType().getTag(),
					photo.getBusiness().getId(),
					photo.getMerchant().getId(),
					Utils.now().getTime(),
					extension);
		else if(photo.getBusiness() != null)
			fileName = String.format(Photo.PHOTO_FILE_NAME,
					env,
					photo.getType().getTag(),
					photo.getBusiness().getId(),
					Utils.now().getTime(),
					extension);
		else
			fileName =  String.format(Photo.PHOTO_FILE_NAME,
					env,
					photo.getType().getTag(),
					Photo.SYS_PHOTO_DIR_ID,
					Utils.now().getTime(),
					extension);
		return fileName;
	}
	
	@RequestMapping(value = "/photo", method = RequestMethod.GET)
    public ResponseEntity<?> getBusinessById(@RequestParam Long id) {
        logger.info("Received request to fetch photos with ID: {}", id);
        try {
            Optional<Photo> photo = galleryService.findByPhotoId(id);
            if (photo.isPresent()) {
                logger.info("Photo found with ID: {}", id);
                return ResponseEntity.ok(photo.get());
            } else {
                logger.warn("No photo found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching photo data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching photo data");
        }
    }
	
	@RequestMapping(value = "/galley_photo", method = RequestMethod.GET)
    public ResponseEntity<?> getGalleryById(@RequestParam Long id) {
        logger.info("Received request to fetch photos with ID: {}", id);
        try {
            Optional<PhotoMerchant> photo = galleryService.findByPhotoMerchantId(id);
            if (photo.isPresent()) {
                logger.info("Photo found with ID: {}", id);
                return ResponseEntity.ok(photo.get());
            } else {
                logger.warn("No photo found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching photo data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching photo data");
        }
    }

    	
}
