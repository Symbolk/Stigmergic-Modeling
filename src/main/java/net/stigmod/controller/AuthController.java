/*
 * Copyright 2014-2016, Stigmergic-Modeling Project
 * SEIDR, Peking University
 * All rights reserved
 *
 * Stigmergic-Modeling is used for collaborative groups to create a conceptual model.
 * It is based on UML 2.0 class diagram specifications and stigmergy theory.
 */

package net.stigmod.controller;

import net.stigmod.domain.system.User;
import net.stigmod.repository.node.UserRepository;
import net.stigmod.service.MailService;
import net.stigmod.service.migrateService.MigrateService;
import net.stigmod.util.config.Config;
import net.stigmod.util.config.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfToken;  // 用于向vm模板中传递csrf token
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;  // 用于向vm模板中传递csrf token
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * Handle StigMod base requests
 *
 * @version     2016/01/23
 * @author 	    Shijun Wang
 */
@Controller
public class AuthController {

    // Common settings
    private Config config = ConfigLoader.load();
    private String host = config.getHost();
    private String port = config.getPort();

    @Autowired
    UserRepository userRepository;

    @Autowired
    MailService mailService;

    @Autowired
    MigrateService migrateService;

    private final static Logger logger = LoggerFactory.getLogger(AuthController.class);

    // sign up page GET
    @RequestMapping(value="/signup", method = RequestMethod.GET)
    public String reg(ModelMap model, HttpServletRequest request) {

        if (migrateService.isRunning()) {
            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Service Unavailable");
            return "service_unavailable";
        }

        model.addAttribute("host", host);
        model.addAttribute("port", port);
        model.addAttribute("title", "Sign Up");

        // CSRF token
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        return "signup";
    }

    // sign up page POST
    @RequestMapping(value="/signup", method = RequestMethod.POST)
    public String regPost(
            @RequestParam(value = "name") String nameISO,
            @RequestParam(value = "mail") String mail,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "password-repeat") String passwordRepeat,
            ModelMap model, HttpServletRequest request) throws UnsupportedEncodingException {

        if (migrateService.isRunning()) {
            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Service Unavailable");
            return "service_unavailable";
        }

        String name = new String(nameISO.getBytes("ISO-8859-1"), "UTF-8");  // 支持中文

        try {
            String verificationId = userRepository.register(name, mail, password, passwordRepeat);

            model.addAttribute("mail", mail);
            model.addAttribute("verificationId", verificationId);
            model.addAttribute("confirmType", "signup");  // 区分是“注册激活”还是“重置密码”
            return "redirect:/checkmail";

        } catch(Exception e) {
            e.printStackTrace();

            // CSRF token
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                model.addAttribute("_csrf", csrfToken);
            }

            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Sign Up");
            model.addAttribute("name", name);
            model.addAttribute("mail", mail);
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    // sign up verification GET
    @RequestMapping(value="/signup/verify", method = RequestMethod.GET)
    public String regVerify(@RequestParam(value = "id") String id, ModelMap model, HttpServletRequest request) {

        if (migrateService.isRunning()) {
            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Service Unavailable");
            return "service_unavailable";
        }

        // CSRF token
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        model.addAttribute("host", host);
        model.addAttribute("port", port);

        try {
            userRepository.registerVerify(id);
            model.addAttribute("title", "Sign In");
            model.addAttribute("success", "Your account has been activated successfully. Please sign in.");
            return "signin";  // 激活账户成功，进入登录页面

        } catch(Exception e) {
            e.printStackTrace();
            model.addAttribute("title", "Sign Up");
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    // check mail page GET
    @RequestMapping(value="/checkmail", method = RequestMethod.GET)
    public String checkMail(@RequestParam("mail") String mail,
                            @RequestParam("verificationId") String verificationId,
                            @RequestParam("confirmType") String confirmType,
                            ModelMap model, HttpServletRequest request) {

        if (migrateService.isRunning()) {
            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Service Unavailable");
            return "service_unavailable";
        }

        model.addAttribute("host", host);
        model.addAttribute("port", port);
        model.addAttribute("title", "Check Mail");
        model.addAttribute("mail", mail);
        model.addAttribute("verificationId", verificationId);
        model.addAttribute("confirmType", confirmType);

        // CSRF token
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        return "check_mail";
    }

    // check mail resend POST
    @RequestMapping(value="/checkmail/resend", method = RequestMethod.POST)
    public String checkMailResend(@RequestParam("mail") String mail,
                                  @RequestParam("verificationId") String verificationId,
                                  @RequestParam("confirmType") String confirmType,
                                  ModelMap model, HttpServletRequest request) {

        if (migrateService.isRunning()) {
            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Service Unavailable");
            return "service_unavailable";
        }

        model.addAttribute("host", host);
        model.addAttribute("port", port);
        model.addAttribute("title", "Check Mail");
        model.addAttribute("mail", mail);
        model.addAttribute("verificationId", verificationId);
        model.addAttribute("confirmType", confirmType);

        try {
            String newVerId;
            if (confirmType.equals("signup")) {
                newVerId =  userRepository.resendRegisterEmail(verificationId);
            } else if (confirmType.equals("forget")) {
                newVerId =  userRepository.resendResetPasswordEmail(verificationId);
            } else {
                throw new IllegalArgumentException("Wrong confirmation email type.");
            }

            model.addAttribute("verificationId", newVerId);
            model.addAttribute("success", "Email resent successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("verificationId", verificationId);
            model.addAttribute("error", e.getMessage());
        }

        // CSRF token
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        return "check_mail";
    }

    // 用户忘记密码页面 GET
    @RequestMapping(value = "/forget", method = RequestMethod.GET)
    public String forget(ModelMap model, HttpServletRequest request) {

        if (migrateService.isRunning()) {
            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Service Unavailable");
            return "service_unavailable";
        }

        // CSRF token
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        model.addAttribute("host", host);
        model.addAttribute("port", port);
        model.addAttribute("title", "Forget Password");
        return "forget_password";
    }

    // 用户忘记密码页面 POST
    @RequestMapping(value = "/forget", method = RequestMethod.POST)
    public String doForget(@RequestParam("mail") String mail, ModelMap model, HttpServletRequest request) {

        if (migrateService.isRunning()) {
            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Service Unavailable");
            return "service_unavailable";
        }

        try {
            String verificationId = userRepository.forgetPassword(mail);
            model.addAttribute("mail", mail);
            model.addAttribute("verificationId", verificationId);
            model.addAttribute("confirmType", "forget");  // 区分是“注册激活”还是“重置密码”
            return "redirect:/checkmail";

        } catch(Exception e) {
            e.printStackTrace();

            // CSRF token
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                model.addAttribute("_csrf", csrfToken);
            }

            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Forget Password");
            model.addAttribute("error", e.getMessage());
            return "forget_password";
        }
    }

    // 用户忘记密码页面 verification GET
    @RequestMapping(value = "/forget/verify", method = RequestMethod.GET)
    public String forgetVerify(@RequestParam(value = "id") String id, ModelMap model, HttpServletRequest request) {

        if (migrateService.isRunning()) {
            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Service Unavailable");
            return "service_unavailable";
        }

        try {
            User user = userRepository.resetPasswordVerify(id);
            model.addAttribute("verificationId", user.getVerificationId());  // 将 ID 加入 resetpassword 页面的 GET 参数
            return "redirect:/resetpassword";  // 邮件验证成功，进入重置密码页面

        } catch(Exception e) {
            e.printStackTrace();

            // CSRF token
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                model.addAttribute("_csrf", csrfToken);
            }

            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Sign In");
            model.addAttribute("error", e.getMessage());
            return "signin";
        }
    }

    // 用户重置密码页面 GET
    @RequestMapping(value = "/resetpassword", method = RequestMethod.GET)
    public String resetPassword(@RequestParam("verificationId") String verificationId,  ModelMap model, HttpServletRequest request) {

        if (migrateService.isRunning()) {
            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Service Unavailable");
            return "service_unavailable";
        }

        // CSRF token
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        model.addAttribute("verificationId", verificationId);  // 用于填写表单中的 hidden input
        model.addAttribute("host", host);
        model.addAttribute("port", port);
        model.addAttribute("title", "Reset Password");
        return "reset_password";
    }

    // 用户重置密码页面 POST
    @RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
    public String doResetPassword(
            @RequestParam(value = "verificationId") String verificationId,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "password-repeat") String passwordRepeat,
            ModelMap model, HttpServletRequest request) {

        if (migrateService.isRunning()) {
            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Service Unavailable");
            return "service_unavailable";
        }

        // CSRF token
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        model.addAttribute("host", host);
        model.addAttribute("port", port);
        model.addAttribute("title", "Sign In");

        try {
            User user = userRepository.resetPassword(verificationId, password, passwordRepeat);
            model.addAttribute("mail", user.getMail());
            model.addAttribute("success", "Reset password successfully.");

        } catch(Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
        }

        return "signin";
    }

    // sign in page GET  (POST route is taken care of by Spring-Security)
    @RequestMapping(value="/signin", method = RequestMethod.GET)
    public String login(@RequestParam(value = "login_error", required = false)String login_error, ModelMap model, HttpServletRequest request) {

        if (migrateService.isRunning()) {
            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Service Unavailable");
            return "service_unavailable";
        }

        model.addAttribute("host", host);
        model.addAttribute("port", port);
        model.addAttribute("title", "Sign In");

        if (login_error != null) {
            model.addAttribute("error", "Sign in failed. Perhaps you have used a wrong email address or wrong password.");
        }

        // CSRF token
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        return "signin";
    }

    // sign out
    @RequestMapping(value="/signout", method = RequestMethod.GET)
    public String logout (HttpServletRequest request, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/signin";
    }

    // Denied page GET
    @RequestMapping(value="/denied", method = RequestMethod.GET)
    public String reg(ModelMap model) {

        if (migrateService.isRunning()) {
            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Service Unavailable");
            return "service_unavailable";
        }

        model.addAttribute("host", host);
        model.addAttribute("port", port);
        model.addAttribute("title", "Denied");

        return "denied";
    }

}