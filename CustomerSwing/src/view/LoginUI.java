package view;

import bussines.UserController;
import core.Helper;
import entity.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI extends JFrame {
    private JPanel container;
    private JPanel pnl_top;
    private JLabel lbl_title;
    private JButton btn_login;
    private JLabel lbl_mail;
    private JLabel lbl_password;
    private JPasswordField fld_password;
    private JTextField fld_mail;
    private UserController userController;

    public LoginUI() {
        this.userController=new UserController();
        this.add(container);
        this.setTitle("Müşteri Yönetim Sistemi");
        this.setSize(400, 300);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        btn_login.addActionListener(e -> {
            JTextField[] checklist = {this.fld_mail, this.fld_password};
            if (Helper.isFieldListEmpty(checklist)) {
                Helper.showMsg("fill");
            } else if (!Helper.isEmailValid(fld_mail.getText())) {
                Helper.showMsg("invalid");
            }
            else {
                User user = this.userController.findByLogin(fld_mail.getText(), fld_password.getText());
                if(user == null) {
                    Helper.showMsg("Girdiğiniz bilgilere göre kullanıcı bulunamadı!");
                }else{
                    this.dispose();
                    DashboardUI dashboardUI = new DashboardUI(user);
                }
            }

        });
    }
}
