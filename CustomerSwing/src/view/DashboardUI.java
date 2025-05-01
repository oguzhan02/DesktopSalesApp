package view;

import bussines.BasketController;
import bussines.CartController;
import bussines.CustomerController;
import bussines.ProductController;
import core.Helper;
import core.Item;
import entity.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.ArrayList;

public class DashboardUI extends JFrame {
    private JPanel container;
    private JLabel lbl_welcome;
    private JButton btn_logout;
    private JTabbedPane tab_menu;
    private JPanel pnl_customer;
    private JScrollPane scrl_customer;
    private JTable tbl_customer;
    private JPanel pnl_customer_filter;
    private JTextField fld_f_customer_name;
    private JComboBox<Customer.TYPE> cmb_f_customer_type;
    private JButton btn_customer_filter;
    private JButton btn_customer_filter_reset;
    private JButton btn_customer_new;
    private JLabel lbl_filter_customer_name;
    private JLabel lbl_filter_customer_type;
    private JPanel pnl_product;
    private JScrollPane scrl_product;
    private JTable tbl_product;
    private JPanel pnl_product_filter;
    private JTextField fld_f_product_name;
    private JTextField fld_f_product_code;
    private JComboBox<Item> cmb_f_product_stock;
    private JButton btn_product_filter;
    private JButton btn_product_filter_reset;
    private JButton btn_product_new;
    private JLabel lbl_f_product_name;
    private JLabel lbl_f_product_code;
    private JLabel lbl_f_product_stock;
    private JPanel pnl_basket;
    private JPanel pnl_basket_top;
    private JScrollPane scrl_basket;
    private JComboBox<Item> cmb_basket_customer;
    private JLabel lbl_customer_choose;
    private JButton btn_basket_reset;
    private JButton btn_basket_new;
    private JLabel lbl_basket_price;
    private JLabel lbl_basket_product_count;
    private JLabel lbl_total_amount;
    private JLabel lbl_product_amount;
    private JTable tbl_basket;
    private JScrollPane scrl_cart;
    private JPanel pnl_cart;
    private JTable tbl_cart;
    private User user;
    private CustomerController customerController;
    private BasketController basketController;
    private CartController cartController;
    private DefaultTableModel tableModelCustomer = new DefaultTableModel();
    private DefaultTableModel tableModelProduct = new DefaultTableModel();
    private DefaultTableModel tableModelBasket = new DefaultTableModel();
    private DefaultTableModel tableModelCart = new DefaultTableModel();
    private bussines.ProductController productController;
    private JPopupMenu popup_customer = new JPopupMenu();
    private JPopupMenu popup_product = new JPopupMenu();

    public DashboardUI(User user) {
        this.user = user;
        this.customerController = new CustomerController();
        this.productController = new ProductController();
        this.basketController = new BasketController();
        this.cartController = new CartController();
        if (user == null) {
            Helper.showMsg("ERROR");
            dispose();
        }
        this.add(container);
        this.setTitle("Müşteri Yönetim Sistemi");
        this.setSize(1200, 650);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        this.lbl_welcome.setText("Hoşgeldin :" + user.getName());
        this.btn_logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginUI loginUI = new LoginUI();
            }
        });
        // CUSTOMER TAB
        loadCustomerTable(null);
        loadCustomerPopupMenu();
        loadCustomerButtonEvent();
        this.cmb_f_customer_type.setModel(new DefaultComboBoxModel<>(Customer.TYPE.values()));
        this.cmb_f_customer_type.setSelectedItem(null);

        // PRODUCT TAB
        loadProductTable(null);
        loadProductPopupMenu();
        loadProductButtonEvent();
        this.cmb_f_product_stock.addItem(new Item(1, "Stokta Var"));
        this.cmb_f_product_stock.addItem(new Item(2, "Stokta Yok"));
        this.cmb_f_product_stock.setSelectedItem(null);

        // BASKET TABB
        loadBasketTable();
        loadBasketButtonEvent();
        loadCustomerBasketCombo();

        // CART TABB
        loadCartTable();

    }
    private void loadCartTable(){
        Object[] columnCart = {"ID","Müşteri Adı","Ürün Adı","Fiyat","Sipariş Tarihi","Not"};
        ArrayList<Cart> carts = this.cartController.findAll();

        // TABLO SIFIRLAMA
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_cart.getModel();
        clearModel.setRowCount(0);
        this.tableModelCart.setColumnIdentifiers(columnCart);
        for (Cart cart : carts) {
            Object[] rowObject = {
                    cart.getId(),
                    cart.getCustomer().getName(),
                    cart.getProduct().getName(),
                    cart.getProduct().getPrice(),
                    cart.getDate(),
                    cart.getNote()
            };
            this.tableModelCart.addRow(rowObject);
        }
        this.tbl_cart.setModel(tableModelCart);
        this.tbl_cart.getTableHeader().setReorderingAllowed(false);
        this.tbl_cart.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_cart.setEnabled(false);
    }
    private void loadCustomerBasketCombo() {
        ArrayList<Customer> customers = this.customerController.findAll();
        this.cmb_basket_customer.removeAllItems();
        for (Customer customer : customers) {
            this.cmb_basket_customer.addItem(new Item(customer.getId(), customer.getName()));
        }
        this.cmb_basket_customer.setSelectedItem(null);
    }

    private void loadBasketTable() {
        Object[] columnBasket = {"ID", "Ürün Adı", "Ürün Kodu", "Fiyat", "Stok Adedi"};
        ArrayList<Basket> basketList = this.basketController.findAll();

        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_basket.getModel();
        clearModel.setRowCount(0);
        this.tableModelBasket.setColumnIdentifiers(columnBasket);
        int totalPrice = 0;
        for (Basket basket : basketList) {
            Object[] rowObject = {
                    basket.getId(),
                    basket.getProduct().getName(),
                    basket.getProduct().getCode(),
                    basket.getProduct().getPrice(),
                    basket.getProduct().getStock()
            };
            this.tableModelBasket.addRow(rowObject);
            totalPrice += basket.getProduct().getPrice();

        }
        this.lbl_basket_price.setText(totalPrice + " TL");
        this.lbl_basket_product_count.setText(basketList.size() + " Adet");
        this.tbl_basket.setModel(tableModelBasket);
        this.tbl_basket.getTableHeader().setReorderingAllowed(false);
        this.tbl_basket.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_basket.setEnabled(false);

    }

    private void loadBasketButtonEvent() {
        this.btn_basket_reset.addActionListener(e -> {
            if (this.basketController.clear()) {
                Helper.showMsg("done");
                loadBasketTable();
            } else {
                Helper.showMsg("error");
            }
        });

        this.btn_basket_new.addActionListener(e -> {
            Item selectedCustomer = (Item) this.cmb_basket_customer.getSelectedItem();
            if(selectedCustomer==null){
                Helper.showMsg("Lütfen Bir Müşteri Seçiniz!");
            }else{
                Customer customer = this.customerController.getById(selectedCustomer.getKey());
                ArrayList<Basket> baskets = this.basketController.findAll();
                if(customer.getId()==0){
                    Helper.showMsg("Böyle Bir Müşteri Bulunamadı");
                }else if(baskets.size()==0) {
                    Helper.showMsg("Lütfen Sepete Ürün Ekleyiniz!");
                }else {
                    CartUI cartUI = new CartUI(customer);
                    cartUI.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            loadBasketTable();
                            loadProductTable(null);
                        }
                    });

                }
            }

        });

    }


    private void loadProductPopupMenu() {
        this.tbl_product.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = tbl_product.rowAtPoint(e.getPoint());
                tbl_product.setRowSelectionInterval(selectedRow, selectedRow);
            }
        });
        this.popup_product.add("Sepete Ekle").addActionListener(e -> {
            int selectedId = Integer.parseInt(this.tbl_product.getValueAt(this.tbl_product.getSelectedRow(), 0).toString());
            Product basketProduct = this.productController.getById(selectedId);
            if (basketProduct.getStock() <= 0) {
                Helper.showMsg("Stokta Yok");
            } else {
                Basket basket = new Basket(basketProduct.getId());
                if (this.basketController.save(basket)) {
                    Helper.showMsg("done");
                    loadBasketTable();
                } else {
                    Helper.showMsg("error");
                }
            }
        });
        this.popup_product.add("Güncelle").addActionListener(e -> {
            int selectedId = Integer.parseInt(this.tbl_product.getValueAt(this.tbl_product.getSelectedRow(), 0).toString());
            ProductUI productUI = new ProductUI(this.productController.getById(selectedId));
            productUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadProductTable(null);
                    loadBasketTable();
                }
            });
        });
        this.popup_product.add("Sil").addActionListener(e -> {
            int selectedId = Integer.parseInt(this.tbl_product.getValueAt(this.tbl_product.getSelectedRow(), 0).toString());
            if (Helper.confirm("sure")) {
                if (this.productController.delete(selectedId)) {
                    Helper.showMsg("done");
                    loadProductTable(null);
                    if (this.tbl_basket != null && this.tableModelBasket != null) {
                        loadBasketTable();
                    }

                } else {
                    Helper.showMsg("error");
                }
            }
        });

        this.tbl_product.setComponentPopupMenu(this.popup_product);
    }

    private void loadProductButtonEvent() {
        this.btn_product_new.addActionListener(e -> {
            ProductUI productUI = new ProductUI(new Product());
            productUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadProductTable(null);
                }
            });
        });

        this.btn_product_filter.addActionListener(e -> {
            ArrayList<Product> filteredProducts = this.productController.filter(
                    this.fld_f_product_name.getText(),
                    this.fld_f_product_code.getText(),
                    (Item) this.cmb_f_product_stock.getSelectedItem()
            );
            loadProductTable(filteredProducts);
        });
        btn_product_filter_reset.addActionListener(e -> {
            loadProductTable(null);
            this.fld_f_product_code.setText(null);
            this.fld_f_product_name.setText(null);
            this.cmb_f_product_stock.setSelectedItem(null);
        });
    }

    private void loadProductTable(ArrayList<Product> products) {
        Object[] columnProduct = {"ID", "Ürün Adı", "Fiyat", "Stok Adedi", "Ürün Kodu"};
        if (products == null) {
            products = this.productController.findAll();
        }
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_product.getModel();
        clearModel.setRowCount(0);
        this.tableModelProduct.setColumnIdentifiers(columnProduct);
        for (Product product : products) {
            Object[] rowObject = {
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getStock(),
                    product.getCode()
            };
            this.tableModelProduct.addRow(rowObject);
            this.tbl_product.setModel(this.tableModelProduct);
            this.tbl_product.getTableHeader().setReorderingAllowed(false);
            this.tbl_product.getColumnModel().getColumn(0).setMaxWidth(50);
            this.tbl_product.setEnabled(true);
        }
    }


    private void loadCustomerButtonEvent() {
        this.btn_customer_new.addActionListener(e -> {
            CustomerUI customerUI = new CustomerUI(new Customer());
            customerUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCustomerTable(null);
                    loadCustomerBasketCombo();
                }
            });
        });
        this.btn_customer_filter.addActionListener(e -> {
            ArrayList<Customer> filteredCustomers = this.customerController.filter(
                    this.fld_f_customer_name.getText(),
                    (Customer.TYPE) this.cmb_f_customer_type.getSelectedItem()
            );
            loadCustomerTable(filteredCustomers);
        });

        btn_customer_filter_reset.addActionListener(e -> {
            loadCustomerTable(null);
            this.fld_f_customer_name.setText(null);
            this.cmb_f_customer_type.setSelectedItem(null);
        });
    }

    private void loadCustomerPopupMenu() {
        this.tbl_customer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = tbl_customer.rowAtPoint(e.getPoint());
                tbl_customer.setRowSelectionInterval(selectedRow, selectedRow);
            }
        });
        this.popup_customer.add("Güncelle").addActionListener(e -> {
            int selectedId = Integer.parseInt(tbl_customer.getValueAt(tbl_customer.getSelectedRow(), 0).toString());
            CustomerUI customerUI = new CustomerUI(this.customerController.getById(selectedId));
            customerUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCustomerTable(null);
                    loadCustomerBasketCombo();
                }
            });
        });
        this.popup_customer.add("Sil").addActionListener(e -> {
            int selectedId = Integer.parseInt(tbl_customer.getValueAt(tbl_customer.getSelectedRow(), 0).toString());
            if (Helper.confirm("sure")) {

                if (this.customerController.delete(selectedId)) {
                    Helper.showMsg("done");
                    loadCustomerTable(null);
                    loadCustomerBasketCombo();
                } else {
                    Helper.showMsg("error");
                }
            }
        });
        this.tbl_customer.setComponentPopupMenu(this.popup_customer);
    }

    public void loadCustomerTable(ArrayList<Customer> customers) {
        Object[] columnCustomer = {"ID", "Müşteri Adı", "Tipi", "Telefon", "E-Posta", "Adres"};
        if (customers == null) {
            customers = this.customerController.findAll();
        }
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_customer.getModel();
        clearModel.setRowCount(0);
        this.tableModelCustomer.setColumnIdentifiers(columnCustomer);
        for (Customer customer : customers) {
            Object[] rowObject = {
                    customer.getId(),
                    customer.getName(),
                    customer.getType(),
                    customer.getPhone(),
                    customer.getMail(),
                    customer.getAdress()
            };
            this.tableModelCustomer.addRow(rowObject);
        }
        this.tbl_customer.setModel(this.tableModelCustomer);
        this.tbl_customer.getTableHeader().setReorderingAllowed(false);
        this.tbl_customer.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_customer.setEnabled(true);
    }
}
