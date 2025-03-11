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

package org.ionspring.vaadin

import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v23.route
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.sidenav.SideNav
import com.vaadin.flow.component.sidenav.SideNavItem
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.RouteParameters
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.vaadin.flow.spring.security.AuthenticationContext
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import kotlin.reflect.KClass

private fun getRouteTitle(routeClass: KClass<*>): String {
    val title = routeClass.java.getAnnotation(PageTitle::class.java)
    return title?.value ?: routeClass.simpleName ?: ""
}

/**
 * Spring <code>ApplicationListener</code> to get the <code>AuthenticationContext</code> needed by Kotlin extension functions.
 */
@Configuration
open class SideNavUtils : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        authenticationContext = try {
            event.applicationContext.getBean(AuthenticationContext::class.java) as AuthenticationContext
        } catch (_: NoSuchBeanDefinitionException) {
            null
        }
    }

    companion object {
        var authenticationContext: AuthenticationContext? = null
    }
}

/**
 * Creates a <code>SideNavItem</code> if the user is authorized to the view and adds it to the SideNav.
 * @param routeClass The <code>KClass</code> of the view.
 * @param icon The <code>VaadinIcon</code> for the route.
 * @param title The title of the route.
 * @param block The customization block for the SideNavItem
 * @return The created SideNavItem, <code>null</code> if user is not authorized.
 */
@VaadinDsl
fun (@VaadinDsl SideNav).securedRoute(
    routeClass: KClass<out Component>,
    icon: VaadinIcon? = null,
    title: String = getRouteTitle(routeClass),
    block: (@VaadinDsl SideNavItem).() -> Unit = {}
): SideNavItem? {
    if (SideNavUtils.authenticationContext == null
        || routeClass.java.isAnnotationPresent(AnonymousAllowed::class.java)
        || (SideNavUtils.authenticationContext!!.isAuthenticated && (
                routeClass.java.isAnnotationPresent(PermitAll::class.java)
                        || (routeClass.java.isAnnotationPresent(RolesAllowed::class.java) &&
                        SideNavUtils.authenticationContext!!.hasAnyRole(*routeClass.java.getAnnotation(RolesAllowed::class.java).value.map {
                            it.removePrefix(
                                "ROLE_"
                            )
                        }.toTypedArray()))
                ))
    ) {
        return route(routeClass, icon, title, RouteParameters.empty(), block)
    }
    return null
}

/**
 * Creates a <code>SideNavItem</code> if the user is authorized to the view and adds it to the SideNavItem.
 * @param routeClass The <code>KClass</code> of the view.
 * @param icon The <code>VaadinIcon</code> for the route.
 * @param title The title of the route.
 * @param block The customization block for the SideNavItem
 * @return The created SideNavItem, <code>null</code> if user is not authorized.
 */
@VaadinDsl
fun (@VaadinDsl SideNavItem).securedRoute(
    routeClass: KClass<out Component>,
    icon: VaadinIcon? = null,
    title: String = getRouteTitle(routeClass),
    block: (@VaadinDsl SideNavItem).() -> Unit = {}
): SideNavItem? {
    if (routeClass.java.isAnnotationPresent(AnonymousAllowed::class.java)
        || (SideNavUtils.authenticationContext!!.isAuthenticated && (
                routeClass.java.isAnnotationPresent(PermitAll::class.java)
                        || (routeClass.java.isAnnotationPresent(RolesAllowed::class.java) &&
                        SideNavUtils.authenticationContext!!.hasAnyRole(*routeClass.java.getAnnotation(RolesAllowed::class.java).value.map {
                            it.removePrefix(
                                "ROLE_"
                            )
                        }.toTypedArray()))
                ))
    ) {
        return route(routeClass, icon, title, RouteParameters.empty(), block)
    }
    return null
}

/**
 * Create a <code>SideNavItem</code> that will be added to the SideNav only if it is not empty. The goal is to not have empty groups
 * if the user is not authorized to any element in the group.
 * @param title The title of the group.
 * @param path The link path for the group.
 * @param block The customization block for the SideNavItem. The block will always be called, if it does not add any child to the group, the group will not be added.
 * @return The created SideNavItem, <code>null</code> if the SideNavItem was empty.
 */
@VaadinDsl
fun (@VaadinDsl SideNav).itemUnlessEmpty(
    title: String,
    path: String? = null,
    block: (@VaadinDsl SideNavItem).() -> Unit = {}
): SideNavItem? {
    val item = SideNavItem(title, path)
    block(item)
    if (item.items.isNotEmpty()) {
        addItem(item)
        return item
    }
    return null
}

/**
 * Create a <code>SideNavItem</code> that will be added to the SideNavItem only if it is not empty. The goal is to not have empty groups
 * if the user is not authorized to any element in the group.
 * @param title The title of the group.
 * @param path The link path for the group.
 * @param block The customization block for the SideNavItem. The block will always be called, if it does not add any child to the group, the group will not be added.
 * @return The created SideNavItem, <code>null</code> if the SideNavItem was empty.
 */
@VaadinDsl
fun (@VaadinDsl SideNavItem).itemUnlessEmpty(
    title: String,
    path: String? = null,
    block: (@VaadinDsl SideNavItem).() -> Unit = {}
): SideNavItem? {
    val item = SideNavItem(title, path)
    block(item)
    if (item.items.isNotEmpty()) {
        addItem(item)
        return item
    }
    return null
}