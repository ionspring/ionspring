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
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.validator.*
import com.vaadin.flow.i18n.I18NProvider
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import java.math.BigDecimal
import java.math.BigInteger
import java.text.NumberFormat


/**
 * Spring <code>ApplicationListener</code> to get the <code>I18NProvider</code> needed by Kotlin extension functions.
 */
@Configuration
open class I18nUtils : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        i18nProvider = try {
            event.applicationContext.getBean(I18NProvider::class.java) as I18NProvider
        } catch (_: NoSuchBeanDefinitionException) {
            null
        }
    }


    companion object {
        var i18nProvider: I18NProvider? = null

        fun getTranslation(key: String, override: String?, fallBack: String, vararg params: Any) =
            override ?: i18nProvider?.getTranslation(key, UI.getCurrent().locale, *params) ?: fallBack
    }
}

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

/**
 * Adds a range validator.
 * @param range the <code>ClosedRange</code> of authorized values
 * @param errorMessage the error message to display. If <code>null</null> the default message will be used
 */
fun <BEAN> Binder.BindingBuilder<BEAN, Float?>.validateInRange(
    range: ClosedRange<Float>,
    errorMessage: String? = null
): Binder.BindingBuilder<BEAN, Float?> =
    withValidator(
        FloatRangeValidator(
            errorMessage ?: I18nUtils.getTranslation(
                "ionspring.validateInRange.errorMessage",
                errorMessage,
                "must be in ${range.start.format()}..${range.endInclusive.format()}", range.start, range.endInclusive
            ),
            range.start,
            range.endInclusive
        )
    )


/**
 * Adds a range validator.
 * @param range the <code>ClosedRange</code> of authorized values
 * @param errorMessage the error message to display. If <code>null</null> the default message will be used
 */
@JvmName("validateIntInRange")
fun <BEAN> Binder.BindingBuilder<BEAN, Int?>.validateInRange(
    range: IntRange,
    errorMessage: String? = null
): Binder.BindingBuilder<BEAN, Int?> =
    withValidator(
        IntegerRangeValidator(
            errorMessage ?: I18nUtils.getTranslation(
                "ionspring.validateInRange.errorMessage",
                errorMessage,
                "must be in ${range.start.format()}..${range.endInclusive.format()}", range.first, range.last
            ), range.first, range.last
        )
    )

/**
 * Adds a range validator.
 * @param range the <code>ClosedRange</code> of authorized values
 * @param errorMessage the error message to display. If <code>null</null> the default message will be used
 */
@JvmName("validateLongInRange")
fun <BEAN> Binder.BindingBuilder<BEAN, Long?>.validateInRange(
    range: LongRange,
    errorMessage: String? = null
): Binder.BindingBuilder<BEAN, Long?> =
    withValidator(
        LongRangeValidator(
            errorMessage ?: I18nUtils.getTranslation(
                "ionspring.validateInRange.errorMessage",
                errorMessage,
                "must be in ${range.start.format()}..${range.endInclusive.format()}", range.first, range.last
            ), range.first, range.last
        )
    )

/**
 * Adds a range validator.
 * @param range the <code>ClosedRange</code> of authorized values
 * @param errorMessage the error message to display. If <code>null</null> the default message will be used
 */
@JvmName("validateDoubleInRange")
fun <BEAN> Binder.BindingBuilder<BEAN, Double?>.validateInRange(
    range: ClosedRange<Double>,
    errorMessage: String? = null
): Binder.BindingBuilder<BEAN, Double?> =
    withValidator(
        DoubleRangeValidator(
            errorMessage ?: I18nUtils.getTranslation(
                "ionspring.validateInRange.errorMessage",
                errorMessage,
                "must be in ${range.start.format()}..${range.endInclusive.format()}", range.start, range.endInclusive
            ), range.start, range.endInclusive
        )
    )

/**
 * Adds a range validator.
 * @param range the <code>ClosedRange</code> of authorized values
 * @param errorMessage the error message to display. If <code>null</null> the default message will be used
 */
@JvmName("validateBigIntegerInRange")
fun <BEAN> Binder.BindingBuilder<BEAN, BigInteger?>.validateInRange(
    range: ClosedRange<BigInteger>,
    errorMessage: String? = null
): Binder.BindingBuilder<BEAN, BigInteger?> =
    withValidator(
        BigIntegerRangeValidator(
            errorMessage ?: I18nUtils.getTranslation(
                "ionspring.validateInRange.errorMessage",
                errorMessage,
                "must be in ${range.start.format()}..${range.endInclusive.format()}", range.start, range.endInclusive
            ), range.start, range.endInclusive
        )
    )

/**
 * Adds a range validator.
 * @param range the <code>ClosedRange</code> of authorized values
 * @param errorMessage the error message to display. If <code>null</null> the default message will be used
 */
@JvmName("validateBigDecimalInRange")
fun <BEAN> Binder.BindingBuilder<BEAN, BigDecimal?>.validateInRange(
    range: ClosedRange<BigDecimal>,
    errorMessage: String? = null
): Binder.BindingBuilder<BEAN, BigDecimal?> =
    withValidator(
        BigDecimalRangeValidator(
            I18nUtils.getTranslation(
                "ionspring.validateInRange.errorMessage",
                errorMessage,
                "must be in ${range.start.format()}..${range.endInclusive.format()}", range.start, range.endInclusive
            ), range.start, range.endInclusive
        )
    )
