package controllers;

import javax.swing.*;

public class MenuController {
    private final ProductController productController = new ProductController();

    public void start(){
        String userChoice = JOptionPane.showInputDialog(this.getMenuItems());
        this.handleUserChoice(userChoice);
        this.start();
    }

    private String getMenuItems() {
        return """
                Welcome to Product Manager
                1. Add product
                2. Display products
                3. Search product
                4. View Single product
                5. Remove product
                6. Update product
                7. Exit
                """;
    }

    private void handleUserChoice(String userChoice) {
        switch (userChoice){
            case "1" -> this.productController.addProduct();
            case "2" -> this.productController.viewAllProducts();
            case "3" -> this.productController.searchProduct();
            case "4" -> this.productController.viewProduct();
            case "5" -> this.productController.removeProduct();
            case "6" -> this.productController.updateProduct();
            case "7" -> System.exit(0);
            default -> JOptionPane.showInputDialog(null,"Please choose an option from the list");
        }
    }

}
