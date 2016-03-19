/*
 * Copyright 2014-2016, Stigmergic-Modeling Project
 * SEIDR, Peking University
 * All rights reserved
 *
 * Stigmergic-Modeling is used for collaborative groups to create a conceptual model.
 * It is based on UML 2.0 class diagram specifications and stigmergy theory.
 */

package net.stigmod.controller;

import net.stigmod.domain.system.SystemInfo;
import net.stigmod.domain.system.User;
import net.stigmod.repository.node.SystemInfoRepository;
import net.stigmod.service.ModelService;
import net.stigmod.service.SessionService;
import net.stigmod.service.migrateService.MigrateService;
import net.stigmod.util.config.Config;
import net.stigmod.util.config.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import net.stigmod.repository.node.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Handle StigMod base requests
 *
 * @version     2015/08/04
 * @author 	    Shijun Wang
 */
@Controller
public class StigModController {

    // Common settings
    private Config config = ConfigLoader.load();
    private String host = config.getHost();
    private String port = config.getPort();

    @Autowired
    UserRepository userRepository;

    @Autowired
    MigrateService migrateService;

    @Autowired
    SessionService sessionService;

    @Autowired
    ModelService modelService;

    @Autowired
    SystemInfoRepository sysRepo;

    private final static Logger logger = LoggerFactory.getLogger(StigModController.class);

    // front page
    @RequestMapping(value="/", method = RequestMethod.GET)
    public String index(ModelMap model) {

        if (migrateService.isRunning()) {
            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Service Unavailable");
            return "service_unavailable";
        }

        model.addAttribute("host", host);
        model.addAttribute("port", port);
        model.addAttribute("title", "Index");
        return "index";
    }

    // about this web app
    @RequestMapping(value="/about", method = RequestMethod.GET)
    public String about(ModelMap model) {

        if (migrateService.isRunning()) {
            model.addAttribute("host", host);
            model.addAttribute("port", port);
            model.addAttribute("title", "Service Unavailable");
            return "service_unavailable";
        }

        final User user = userRepository.getUserFromSession();
        model.addAttribute("user", user);
        model.addAttribute("host", host);
        model.addAttribute("port", port);
        model.addAttribute("title", "About");
        return "about";
    }

    // about this web app
    @RequestMapping(value="/testFusion/{ccmId}", method = RequestMethod.GET)
    @ResponseBody
    public long testFusion(@PathVariable("ccmId") Long ccmId) {
//        migrateService.migrateAlgorithmImpls(ccmId);
        return sessionService.getOnlineUserNumber();
//        return "Fusion Finished";
    }


    // admin page GET
    @RequestMapping(value="/nimda/{password}", method = RequestMethod.GET)
    public String admin(@PathVariable("password") String password, ModelMap model, HttpServletRequest request) {

        // CSRF token
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        model.addAttribute("host", host);
        model.addAttribute("port", port);

        if (password.equals(config.getAdminPassword())) {

            SystemInfo systemInfo = sysRepo.getSystemInfo();
            model.addAttribute("activatedCcmName", systemInfo.getActivatedCcmName());
            model.addAttribute("activatedCcmId", systemInfo.getActivatedCcmId());

            model.addAttribute("ccms", modelService.getAllCcmNamesAndIds());
            model.addAttribute("title", "Admin");
            return "admin";
        } else {

            model.addAttribute("title", "Denied");
            return "denied";
        }
    }

    // admin page POST
    @RequestMapping(value="/nimda/{password}", method = RequestMethod.POST)
    public String adminPost(@RequestParam(value = "nameAndId") String nameAndId,
                            @PathVariable("password") String password, ModelMap model, HttpServletRequest request) {

        // CSRF token
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        model.addAttribute("host", host);
        model.addAttribute("port", port);

        if (password.equals(config.getAdminPassword())) {

            try {
                modelService.setAsActivatedCcm(nameAndId);
                model.addAttribute("success", "Set " + nameAndId + " as activated CCM successfully.");

                String[] nameAndIdArray = nameAndId.split("-");
                model.addAttribute("activatedCcmName", nameAndIdArray[0]);
                model.addAttribute("activatedCcmId", nameAndIdArray[1]);

            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", e.getMessage());
            }

            model.addAttribute("ccms", modelService.getAllCcmNamesAndIds());
            model.addAttribute("title", "Admin");
            return "admin";
        } else {

            model.addAttribute("title", "Denied");
            return "denied";
        }
    }


    // favicon
    @RequestMapping("/favicon.ico")
    public String favicon() {
        return "forward:/static/dist/img/favicon.ico";
    }

}