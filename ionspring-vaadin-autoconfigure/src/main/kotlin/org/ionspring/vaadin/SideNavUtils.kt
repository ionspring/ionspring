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
import com.vaadin.flow.server.auth.AnonymousAllowed
import com.vaadin.flow.spring.security.AuthenticationContext
import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.reflect.KClass

private fun getRouteTitle(routeClass: KClass<*>): String {
    val title = routeClass.java.getAnnotation(PageTitle::class.java)
    return title?.value ?: routeClass.simpleName ?: ""
}

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
        return route(routeClass, icon, title, block)
    }
    return null
}

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
        return route(routeClass, icon, title, block)
    }
    return null
}

@VaadinDsl
public fun (@VaadinDsl SideNav).itemUnlessEmpty(
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

@VaadinDsl
public fun (@VaadinDsl SideNavItem).itemUnlessEmpty(
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