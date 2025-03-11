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

import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.theme.lumo.LumoUtility

class Toolbar(
    searchBar: Boolean = true,
    refreshButton: Boolean = true,
    addButton: Boolean = true
) : KComposite() {
    var onAdd = {}
    var onRefresh = {}
    var onSearch = { _: String -> }
    lateinit var searchField: TextField
    lateinit var refreshButton: Button
    lateinit var addButton: Button

    private val root = ui {
        flexLayout {
            if (searchBar) {
                searchField = textField {
                    prefixComponent = Icon(VaadinIcon.SEARCH)
                    placeholder = "Search"
                    isExpand = true
                    valueChangeMode = ValueChangeMode.LAZY
                    addValueChangeListener { event ->
                        onSearch(event.value)
                    }
                }
            }
            if (refreshButton) {
                this@Toolbar.refreshButton = button(null, Icon(VaadinIcon.REFRESH)) {
                    addClassNames(LumoUtility.Margin.Left.SMALL)
                    addClickShortcut(Key.F5)
                    addClickListener { onRefresh() }
                }
            }
            if (addButton) {
                this@Toolbar.addButton = button(null, Icon(VaadinIcon.PLUS)) {
                    addClassNames(LumoUtility.Margin.Left.SMALL)
                    setPrimary()
                    addClickShortcut(Key.F6)
                    addClickListener { onAdd() }
                }
            }
            setWidthFull()
        }
    }
}

@VaadinDsl
fun (@VaadinDsl HasComponents).toolbar(
    searchBar: Boolean = true,
    refreshButton: Boolean = true,
    addButton: Boolean = true,
    block: (@VaadinDsl Toolbar).() -> Unit = {}
) = init(Toolbar(searchBar, refreshButton, addButton), block)