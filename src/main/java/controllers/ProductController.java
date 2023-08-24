package controllers;

import dto.Category;
import dto.Product;
import services.ProductService;

import javax.swing.*;
import java.util.*;

public class ProductController {
    private final ProductService productService = new ProductService();
    private ArrayList<Product> products;


    private final Comparator<Product> comparatorPriceFromMin = new Comparator<Product>() {
        @Override
        public int compare(Product o1, Product o2) {
            if (o1.getPrice() == o2.getPrice()) return 0;
            return o1.getPrice() > o2.getPrice() ? 1: -1;
        }
    };
    private final Comparator<Product> comparatorPriceFromMax = new Comparator<Product>() {
        @Override
        public int compare(Product o1, Product o2) {
            if (o1.getPrice() == o2.getPrice()) return 0;
            return o1.getPrice() < o2.getPrice() ? 1: -1;
        }
    };
    private final Comparator<Product> comparatorCategory = new Comparator<Product>() {
        @Override
        public int compare(Product o1, Product o2) {
            if (o1.getCategory() == o2.getCategory()) return 0;
            return o1.getCategory().toString().compareTo(o2.getCategory().toString());
        }
    };
    private final Comparator<Product> comparatorName = new Comparator<Product>() {
        @Override
        public int compare(Product o1, Product o2) {
            if (o1.getName() == o2.getName()) return 0;
            return o1.getName().compareTo(o2.getName());
        }
    };

    public ProductController(){
        this.renewProductList();
    }

    private void renewProductList(){
        try {
            this.products = (ArrayList<Product>) this.productService.getAllProducts();
        } catch (Exception e){
            this.displayMessage(e.getMessage());
        }
    }
    public void addProduct() {
        try{
            Product product = this.collectProductInfo();
            this.productService.createProduct(product);
            this.renewProductList();
            this.displayMessage("Product was created successfully");
        } catch (Exception e){
            this.displayMessage(e.getMessage());
        }
    }

    private Product collectProductInfo() {
        Product product = new Product();
        product.setName(this.getUserInput("Name the product"));
        product.setPrice(Float.valueOf(this.getUserInput("Set price of the product")));
        String selectedCategory = (String) this.getUserInputFromDropDown(
                Arrays.stream(Category.values()).map(Category::name).toArray(),
                "Choose product category",
                "What is category of this product?"
        );
        product.setCategory(Category.valueOf(selectedCategory));
        return product;
    }

    private Product collectProductInfo(Product initialProduct) {
        Product product = new Product();
        product.setName(JOptionPane.showInputDialog("Name the product", initialProduct.getName()));
        product.setPrice(Float.valueOf(JOptionPane.showInputDialog("Set price of the product", initialProduct.getPrice())));
        String selectedCategory = (String) this.getUserInputFromDropDownWithInitialValue(
                Arrays.stream(Category.values()).map(Category::name).toArray(),
                "Choose product category",
                "What is category of this product?",
                initialProduct.getCategory()
        );
        product.setCategory(Category.valueOf(selectedCategory));
        return product;
    }

    private Object getUserInputFromDropDown(Object[] dropDownOptions, String title, String message){
        return JOptionPane.showInputDialog(
                null,
                message,
                title,
                JOptionPane.QUESTION_MESSAGE,
                null,
                dropDownOptions,
                dropDownOptions[0]
        );
    }
    private Object getUserInputFromDropDownWithInitialValue(Object[] dropDownOptions, String title, String message, Category category){
        int categoryIndex = 0;
        for(int i=0; i<dropDownOptions.length; i++ ){
            String opt = (String) dropDownOptions[i];
            if (opt.equals(category.toString())) {
                categoryIndex = i;
                break;
            }
        }
        return JOptionPane.showInputDialog(
                null,
                message,
                title,
                JOptionPane.QUESTION_MESSAGE,
                null,
                dropDownOptions,
                dropDownOptions[categoryIndex]
        );
    }

    private void sortingProduct(){
        String[] sortingType = new String[]{
                "By price from min to max", "By price from max to min",
                "By category A-Z" , "By category Z-A",
                "By name A-Z", "By name Z-A"
            };
        String selectedSorting = (String) this.getUserInputFromDropDown(
                sortingType,
                "Choose way of sorting",
                "What is the way of sorting?"
        );
        switch (selectedSorting){
            case "By price from min to max" -> Collections.sort(this.products, comparatorPriceFromMin);
            case "By price from max to min" -> Collections.sort(this.products, comparatorPriceFromMax);
            case "By category A-Z" -> Collections.sort(this.products, comparatorCategory);
            case "By category Z-A" -> {
                        Collections.sort(this.products, comparatorCategory);
                        Collections.reverse(this.products);
                    }
            case "By name A-Z" -> Collections.sort(this.products, comparatorName);
            case "By name Z-A" -> {
                        Collections.sort(this.products, comparatorName);
                        Collections.reverse(this.products);
                    }
        }
    }
    public void viewAllProducts() {
        try{
            this.sortingProduct();
            StringBuilder productsAsString = new StringBuilder();
            for(Product prod: this.products){
                productsAsString.append(prod.toString());
            }
            this.displayMessage(productsAsString.toString());
        } catch (NullPointerException e){
            this.displayMessage("You don't have any products yet, please, add products");
        } catch (Exception e){
            this.displayMessage(e.getMessage());
        }
    }
    public void searchProduct() {
        String stringToFind = this.getUserInput("Please, enter name or category you want to find").toLowerCase();
        StringBuilder productsAsString = new StringBuilder();
        for(Product product: this.products){
            if(product.getName().toLowerCase().contains(stringToFind)
                    || product.getCategory().toString().toLowerCase().contains(stringToFind)){
                productsAsString.append(product.toString());
            }
        }
        this.displayMessage(productsAsString.toString());
    }

    private String getUserInput(String s) {
        return JOptionPane.showInputDialog(s);
    }
    public void viewProduct() {
        try{
            List<Product> existingProduct = this.productService.getAllProducts();
            Product selectedProduct = (Product) this.getUserInputFromDropDown(
                    existingProduct.toArray(),
                    "View product",
                    "Choose the product to view"
            );
            this.displayMessage(
                    new StringBuilder()
                            .append("Name: \t").append(selectedProduct.getName()).append("\n")
                            .append("Price: \t").append(selectedProduct.getPrice()).append("\n")
                            .append("Status: \t").append(selectedProduct.getCategory()).append("\n")
                            .append("ID: \t").append(selectedProduct.get_id()).append("\n")
                            .toString()
            );
        }catch (Exception exception){
            this.displayMessage(exception.getMessage());
        }
    }

    public void removeProduct() {
        try {
            List<Product> existingProduct = this.productService.getAllProducts();
            Product selectedProduct = (Product) this.getUserInputFromDropDown(
                    existingProduct.toArray(),
                    "Remove product",
                    "Choose product to remove"
            );
            try {
                this.productService.deleteProduct(selectedProduct.get_id());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.renewProductList();
            this.displayMessage("Product removed successfully");
        } catch(Exception e){
            this.displayMessage(e.getMessage());
        }
    }

    public void updateProduct() {
        try{
            Product selectedProduct = (Product) this.getUserInputFromDropDown(
                    this.products.toArray(),
                    "Update product",
                    "Choose the product to update"
            );
            Product product = this.collectProductInfo(selectedProduct);
            this.productService.updateProduct(product, selectedProduct.get_id());
            this.renewProductList();
            this.displayMessage("Product was updated successfully");
        } catch (Exception e){
            this.displayMessage(e.getMessage());
        }
    }


    private void displayMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}
