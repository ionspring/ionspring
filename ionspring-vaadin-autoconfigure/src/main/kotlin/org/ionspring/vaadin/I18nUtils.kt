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

import com.vaadin.flow.component.UI
import java.text.NumberFormat

/**
 * Formats a number according to the current user locale.
 * @param minDecimals Minimum number of digits in the fractional part of the number. If <code>null</code>, no minimum is set.
 * @param maxDecimals Maximum number of digits in the fractional part of the number. If <code>null</code>, the maximum will be set to the same as the minimum if it is set, otherwise, no maximum is set.
 *
 * @return The formated number.
 */
fun Number.format(minDecimals: Int? = null, maxDecimals: Int? = null): String {
    val locale = UI.getCurrent()?.locale
    val decimalFormat = NumberFormat.getNumberInstance(locale).apply {
        if (minDecimals != null) {
            minimumFractionDigits = minDecimals
            maximumFractionDigits = if (maxDecimals == null) {
                minDecimals
            } else {
                maxDecimals
            }
        } else if (maxDecimals != null) {
            maximumFractionDigits = maxDecimals
        }
    }
    return decimalFormat.format(this)
}