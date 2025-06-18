package com.SCM.photo;



import java.util.Date;

import com.SCM.controllers.Merchant;
import com.SCM.entities.Business;
import com.SCM.utils.BigBaseEntity;
import com.SCM.utils.Utils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "photo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Photo extends BigBaseEntity{
	
	public static final String OUTLET_PHOTO_FILE_NAME = "%s/%s/%d/%d/%d.%s";
	public static final String PHOTO_FILE_NAME = "%s/%s/%d/%d.%s";
	public static final int PHOTO_LIMIT =5;
	public static final int SYS_PHOTO_DIR_ID = 0;
	
	public static enum PhotoType {
		LOGO("logo", 30, 120, 1),
	    BANNER("banner", 750, 400, 4),
	    PRODUCT("product", 342, 228, 5),
	    BACKGROUND("background", 1921, 1081, 1),
	    BILL("bill", 1921, 1081, Integer.MAX_VALUE),
	    EXT_VOUCHER("ext_voucher", 30, 120, Integer.MAX_VALUE),
	    STAGING("staging", 0, 0, Integer.MAX_VALUE),
	    VIDEO_TESTIMONIAL("video_testimonial", 0, 0, Integer.MAX_VALUE),
	    ITEM("item", 30, 120, Integer.MAX_VALUE),
	    PWA_LOGO_S("pwa_logo_s", 192, 192, 1),
	    PWA_LOGO_L("pwa_logo_l", 512, 512, 1),
	    CUSTOMER_PROFILE("customer_profile", 200, 150, 2),
	    CLOTHES("clothes", 300, 300, 10),
	    FOOD("food", 300, 300, 10),
	    SHOES("shoes", 300, 300, 10),
	    
	    // Clothing categories
	    CLOTHING("Clothing", 300, 300, 10),
	    ACCESSORIES("Accessories", 300, 300, 10),
	    FOOTWEAR("Footwear", 300, 300, 10),
	    JEWELRY("Jewelry", 300, 300, 10),

	    // Restaurant categories
	    RESTAURANT("Restaurant", 300, 300, 10),
	    CAFE("Cafe", 300, 300, 10),
	    FAST_FOOD("Fast Food", 300, 300, 10),
	    FINE_DINING("Fine Dining", 300, 300, 10),
	    CASUAL_DINING("Casual Dining", 300, 300, 10),

	    // Technology categories
	    ELECTRONICS("Electronics", 300, 300, 10),
	    COMPUTERS("Computers", 300, 300, 10),
	    MOBILE_PHONES("Mobile Phones", 300, 300, 10),
	    SOFTWARE("Software", 300, 300, 10);
		
		String tag;
		int width;
		int height;
		int limit;
		
		private PhotoType(String tag, int width, int height, int limit) {
			this.tag = tag;
			this.width = width;
			this.height = height;
			this.limit = limit;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getLimit() {
			return limit;
		}

		public void setLimit(int limit) {
			this.limit = limit;
		}
	
	}
	
	@ManyToOne
	@JoinColumn(name = "business_id")
	private Business business;
	
	@ManyToOne
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;
	
	@Column(name = "file_name")
	String fileName;
	
	@Column(name = "content_type")
	String contentType;
	
	@Column(name = "size")
	Integer size;
	
	@Column(name = "end_point")
	String endPoint;
	
	@Column(name= "type")
	@Enumerated(EnumType.ORDINAL)
	PhotoType type;
	
	@Column(name = "sequence")
	Long sequence;
	
	boolean deleted;
	
	//@Transient
	
	@Column(name = "image_data_src")
	String imgDataSrc;
	
	@PrePersist
    protected void onCreate() {
        Date now = Utils.now();
        if (this.getCreationTime() == null)
            this.setCreationTime(now);
        if (this.getUpdateTime() == null)
            this.setUpdateTime(now);
    }

    @PreUpdate
    protected void onUpdate() {
        this.setUpdateTime(Utils.now());
    }
	
	@Transient
	private PhotoExtras photoExtras;
	
	@Column(name = "extras")
	private String photoExtrasScript;

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public PhotoType getType() {
		return type;
	}

	public void setType(PhotoType type) {
		this.type = type;
	}

	public Long getSequence() {
		return sequence;
	}

	public void setSequence(Long sequence) {
		this.sequence = sequence;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public PhotoExtras getPhotoExtras() {
		return PhotoExtras.fromJSON(photoExtrasScript);
	}

	public void setPhotoExtras(PhotoExtras photoExtras) {
		this.photoExtras = photoExtras;
		if(photoExtras != null)
			this.photoExtrasScript = photoExtras.toJSON();
		else
			this.photoExtrasScript = null;
	}

	public String getPhotoExtrasScript() {
		return photoExtrasScript;
	}

	public void setPhotoExtrasScript(String photoExtrasScript) {
		this.photoExtrasScript = photoExtrasScript;
		this.photoExtras = PhotoExtras.fromJSON(photoExtrasScript);
	}

	public String getImgDataSrc() {
		return imgDataSrc;
	}

	public void setImgDataSrc(String imgDataSrc) {
		this.imgDataSrc = imgDataSrc;
	}

	@Transient
	private String title;
	
	@Transient
	private String price;
	
	@Transient
	private String description;
	
	@Transient
	private byte[] imgData;

    public byte[] getImgData() {
        return imgData;
    }

    public void setImgData(byte[] imgData) {
        this.imgData = imgData;
    }
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	
	
}

