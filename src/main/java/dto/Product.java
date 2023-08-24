package dto;

import lombok.Data;

@Data
public class Product {
    private String _id;
    private String name;
    private Float price;
    private Category category;

    @Override
    public String toString() {
        return category + ": " + name + " - " + price + "\n";
    }
}
