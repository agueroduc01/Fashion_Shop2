package fashion_shop.bean;

public class Cart {
	private int idItem;
	private String nameItem;
	private float priceItem;
	private int quality;
	private String image;

	public int getIdItem() {
		return idItem;
	}

	public void setIdItem(int idItem) {
		this.idItem = idItem;
	}

	public String getNameItem() {
		return nameItem;
	}

	public void setNameItem(String nameItem) {
		this.nameItem = nameItem;
	}

	public float getPriceItem() {
		return priceItem;
	}

	public void setPriceItem(float priceItem) {
		this.priceItem = priceItem;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
