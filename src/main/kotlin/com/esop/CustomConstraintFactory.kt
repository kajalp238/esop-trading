package com.esop

import com.esop.validators.EmailValidator
import com.esop.validators.PhoneNumberValidator
import com.esop.validators.UsernameValidator
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import io.micronaut.context.annotation.Factory
import io.micronaut.validation.validator.constraints.ConstraintValidator
import jakarta.inject.Singleton
import com.esop.service.UserService
@Factory
class CustomConstraintFactory {
    @Singleton
    fun phoneNumberValidator() : ConstraintValidator<PhoneNumberValidator, String> {
        val phoneUtil = PhoneNumberUtil.getInstance()

        return ConstraintValidator { value, annotation, context ->
            value == null || try {
                 phoneUtil.isValidNumber(phoneUtil.parse(value, null))
            } catch (e: NumberParseException) {
                context.messageTemplate(e.message)
                false
            }
        }
    }
    @Singleton
    fun userNameValidator() : ConstraintValidator<UsernameValidator, String> {
        val username = UserService()
        return ConstraintValidator { value, annotation, context ->
            username.check_username(value)
        }
    }

    @Singleton
    fun emailValidator() : ConstraintValidator<EmailValidator, String> {
        return ConstraintValidator { value, annotation, context ->
            value == null || validate(value)
        }
    }

    fun validate(email :String): Boolean {
        val p = Regex("^[a-z0-9-_]+[\"]")
        if(p.containsMatchIn(email)){
            return false
        }
        val EMAIL_REGEX = "^(?=[\"\'a-zA-Z0-9][\"\\s\'a-zA-Z0-9@._%+-]{5,253}\$)[\\s\"\'a-zA-Z0-9._%+-]{1,64}@(?:(?=[a-zA-Z0-9-]{1,63}\\.)[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*\\.){1,8}[a-z0-9A-Z]{2,63}\$"
        val pattern = Regex(EMAIL_REGEX)
        return pattern.containsMatchIn(email)
    }
}