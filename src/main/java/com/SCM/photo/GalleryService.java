package com.SCM.photo;

import org.springframework.stereotype.Service;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.entities.User;
import com.SCM.photo.Photo.PhotoType;
import com.SCM.repository.BusinessRepository;
import com.SCM.role.Role.RoleType;
import com.SCM.service.CommonService;
import com.SCM.utils.Utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



@Service
public class GalleryService {

	@Value("${com.crm.config.env}")
	private String env;

	@Autowired
	PhotoRepository photoRepository;
	
	@Autowired
	BusinessRepository businessRepository;
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	PhotoMerchantRepository photoMerchantRepository;
	
	final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
	
	public Photo addPhoto(Photo photo, String extension,byte[] content) {
		
		logger.debug("Add/Replace Photo : {}, extension: ", photo,extension);
		
		// If required Delete existing image (to Replace);
		
		if(photo.getMerchant() != null) {
			deletePhoto(photo.getMerchant(), photo.getType(), photo.getSequence());
		}else if(photo.getBusiness() != null) {
			deletePhoto(photo.getBusiness(), photo.getType(), photo.getSequence());
		}else {
			deletePhoto(photo.getType(), photo.getSequence());
		}
		
		if(!checkLimit(photo)) {
			logger.debug("Photo Store Limit Exceeds for {}", photo.getBusiness().getName());
			return null;
		}
		
		String fileName = generateFileName(photo,extension);
		photo.setFileName(fileName);
		
		// create Object with public/private Read Permission
		boolean privateRead = false;
		if(photo.getType() == PhotoType.STAGING)
			privateRead = true;
		
		//s3Connector.upload(photoBucket, fileName, content, photo.getContentType(), photo.getSize(),privateRead);
		
		// Save Photo Entry
		photo = photoRepository.save(photo);
		logger.debug("Created Photo : " + photo);
		return photo;
	}

	
	public boolean updatePhotoExtras(Merchant merchant, Long sequence, PhotoExtras photoExtras) {
		logger.debug("Update Photo extras : {} for Merchant: {} ", photoExtras, merchant.getName());
		return updatePhotoExtras(null,merchant,sequence,photoExtras);
	}

	public boolean updatePhotoExtras(Business business, Long sequence, PhotoExtras photoExtras) {
		logger.debug("Update Photo extras : {} for Merchant: {} ", photoExtras, business.getName());
		return updatePhotoExtras(business,null,sequence,photoExtras);
	}

	public boolean updatePhotoExtras(Business business, Merchant merchant, Long sequence, PhotoExtras photoExtras) {
		
		Photo photo = null;
		
		if(merchant != null)
			photo = photoRepository.findByMerchantAndTypeAndSequenceAndDeleted(merchant,PhotoType.PRODUCT, sequence, false);
		else
			photo = photoRepository.findByBusinessAndTypeAndSequence(business.getId(),PhotoType.PRODUCT, sequence);
		
		if(photo == null) {
			logger.debug("Unable to update Photo extras, No Photo Found");
			return false;
		}
		
		if(photoExtras != null) {
			if((photoExtras.getTitle() == null || photoExtras.getTitle().isEmpty())
					&& (photoExtras.getLink() == null || photoExtras.getLink().isEmpty())
					&& (photoExtras.getPrice() == null || photoExtras.getPrice().isEmpty())
					&& (photoExtras.getDescription() == null || photoExtras.getDescription().isEmpty()))
				photoExtras = null;
		}
		
		photo.setPhotoExtras(photoExtras);
		photoRepository.save(photo);	
		return true;
	}
	
	public boolean deletePhoto(Merchant merchant, PhotoType type, Long sequence) {
		logger.debug("Delete Photo of Type: {}, Sequence : {} for Merchant : {} ", type, sequence, merchant.getName());
		return deletePhoto(null,merchant, type, sequence);
	}
	
	public boolean deletePhoto(Business business, PhotoType type, Long sequence) {
		logger.debug("Delete Photo of Type: {}, Sequence : {} for Merchant : {} ", type, sequence, business.getName());
		return deletePhoto(business, null, type, sequence);
	}
	
	public boolean deletePhoto(PhotoType type, Long sequence) {
		logger.debug("Delete System Photo of Type: {}, Sequence : {}", type, sequence);
		return deletePhoto(null,null,type, sequence);
	}
	
	
	public boolean deletePhoto(Business business, Merchant merchant, PhotoType type, Long sequence) {
		
		if(type == PhotoType.LOGO
				|| type == PhotoType.PWA_LOGO_S
				|| type == PhotoType.PWA_LOGO_L
				|| type == PhotoType.BACKGROUND) {
			
			List<Photo> photos = null;
			
			if(merchant != null)
				photos = photoRepository.findByMerchantAndTypeAndDeletedOrderBySequence(merchant, type, false);
			else
				photos = photoRepository.findByBusinessAndType(business.getId(),type);
			
			if(photos == null || photos.size() > 0) {
				return false;
			}
			
			//deletePhoto(photos.get(0));
			//return true;
		}
		
		if(type.limit > 1 && sequence == null)
			return false;
		
		Photo photo = null;
		if(merchant != null)
			photo = photoRepository.findByMerchantAndTypeAndSequenceAndDeleted(merchant,type,sequence,false);
		else if(business != null)
			photo = photoRepository.findByBusinessAndTypeAndSequence(business.getId(),type,sequence);
		else
			photo = photoRepository.findByTypeAndSequence(type,sequence);
			
		if(photo == null)
			return false;
		
		deletePhoto(photo);
		
		return true;
	}
	
//	public void deletePhoto(Photo photo) {
//		logger.debug("Dalete Photo : " + photo);
//		s3Connector.delete(photoBucket, photo.getFileName());
//		photo.setDeleted(true);
//		photoRepository.save(photo);
//	}
	
	public void deletePhoto(Photo photo) {
		logger.debug("Dalete Photo : " + photo);
		//s3Connector.delete(photoBucket, photo.getFileName());
		photo.setDeleted(true);
		photoRepository.save(photo);
	}
	
	public Photo getLogo(Business business) {
		List<Photo> photos = photoRepository.findByBusinessAndType(business.getId(),PhotoType.LOGO);
		if(photos != null && photos.size() > 0)
			return photos.get(0);
		return null;
	}
	
	public List<Photo> getBanners(Business business) {
		return getPhotos(business,PhotoType.BANNER);
	}
	
	public List<Photo> getPhotos(Business business,PhotoType type) {
		List<Photo> photos = photoRepository.findByBusinessAndType(business.getId(), type);
		
		//Sort by sequence
		if(photos != null)
			Collections.sort(photos , new Comparator<Photo>() {

				@Override
				public int compare(Photo o1, Photo o2) {
					
					return o1.getSequence().compareTo(o2.getSequence());
				}
				
			});
		
		return photos;
	}
	
	public boolean checkLimit(Photo photo) {
		Integer count = null;
		
		if(photo.getMerchant() != null) {
			count = photoRepository.countByMerchant(photo.getMerchant().getId(), photo.getType());
		}
		else if(photo.getBusiness() != null) {
			count = photoRepository.countByBusiness(photo.getBusiness().getId(), photo.getType());
		}else {
			count = photoRepository.countByType(photo.getType());
		}
		
		Integer limit = photo.getType().getLimit();
		
		return count < limit;
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
	   
	public void authorize() {
		User user = commonService.getLoggedInUser();
		
		if(user.getRole().getName() != RoleType.ROLE_ADMIN
				&& user.getRole().getName() != RoleType.ROLE_MERCHANT_ADMIN) {
			throw new RuntimeException("Not Allowed");
		}
		
		if(user.getRole().getName() == RoleType.ROLE_ADMIN)
			return;
		
		Business business = businessRepository.findById(user.getBusiness().getId()).get();
		
	}


	 public List<Photo> getAllPhotos() {
	        return (List<Photo>) photoRepository.findAll();
	    }


	public Optional<Photo> findByPhotoId(Long id) {
		logger.info("Fetching photo with userId: {}", id);

        try {
            Optional<Photo> photo = photoRepository.findById(id);
            if (photo.isPresent()) {
                logger.info("Photo found with userId: {}", id);
            } else {
                logger.warn("No Photo found with userId: {}", id);
            }
            return photo;
        } catch (Exception e) {
            logger.error("Error fetching photo with userId: {}", id, e);
            throw e;
        }
	}
	
	public Optional<PhotoMerchant> findByPhotoMerchantId(Long id) {
		logger.info("Fetching photo with userId: {}", id);

        try {
            Optional<PhotoMerchant> photo = photoMerchantRepository.findById(id);
            if (photo.isPresent()) {
                logger.info("Photo found with userId: {}", id);
            } else {
                logger.warn("No Photo found with userId: {}", id);
            }
            return photo;
        } catch (Exception e) {
            logger.error("Error fetching photo with userId: {}", id, e);
            throw e;
        }
	}
	
}

