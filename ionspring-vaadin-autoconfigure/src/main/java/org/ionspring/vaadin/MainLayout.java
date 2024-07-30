/*
 * Copyright 2024 Damien Ferrand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ionspring.vaadin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class MainLayout extends AppLayout {
    private final transient AuthenticationContext authenticationContext;

    public MainLayout(@Autowired(required = false) AuthenticationContext authenticationContext,
                      @Autowired(required = false) @Qualifier("navigation") Component navigation) {

        this.authenticationContext = authenticationContext;

        H3 logo = new H3(getTranslation("ionspring.layout.title"));
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.Vertical.AUTO, LumoUtility.Margin.Left.SMALL);
        final HorizontalLayout header = new HorizontalLayout();

        if (authenticationContext != null && authenticationContext.isAuthenticated()) {
            Span spacer = new Span();
            spacer.addClassNames(LumoUtility.Margin.Left.AUTO);
            Span loggedAs = new Span(getTranslation("ionspring.layout.loggedInAs",
                    authenticationContext.getPrincipalName().get()));
            loggedAs.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.Vertical.AUTO,
                    LumoUtility.Display.HIDDEN, LumoUtility.Display.Breakpoint.Medium.FLEX);
            Button logout = new Button(getTranslation("ionspring.layout.logout"), click ->
                    authenticationContext.logout()
            );
            logout.addClassNames(LumoUtility.Margin.Right.SMALL);
            if (navigation != null) {
                final DrawerToggle toggle = new DrawerToggle();
                header.add(toggle);
                addToDrawer(navigation);
            }
            header.add(logo, spacer, loggedAs, logout);
        } else {
            header.add(logo);
        }
        header.setWidth("100%");
        addToNavbar(header);
    }

}
