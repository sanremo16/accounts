package org.san.home.accounts.service.error;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import java.util.*;

/**
 * @author sanremo16
 */
public class ErrorMessageSource implements MessageSource {

    private List<ResourceBundle> resourceBundles = new ArrayList<>();

    public ErrorMessageSource() {
        for (ErrorMessageResource resource : ErrorMessageResource.values()) {
            addResourceBundleByName(resource.getFileName());
        }
    }

    private void addResourceBundleByName(String name) {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("localize." + name, Locale.getDefault(), Thread.currentThread().getContextClassLoader());
            resourceBundles.add(resourceBundle);
        } catch (Throwable ignored) {
        }
    }

    @Override
    public String getMessage(String s, Object[] objects, String s2, Locale locale) {
        for (ResourceBundle resourceBundle : resourceBundles) {
            try {
                return resourceBundle.getString(s);
            } catch (MissingResourceException ignored) {
            }
        }
        return s2;
    }

    @Override
    public String getMessage(String s, Object[] objects, Locale locale) throws NoSuchMessageException {
        String result = getMessage(s, objects, "", locale);
        if (result.isEmpty())
            throw new NoSuchMessageException(s, locale);
        return result;
    }

    @Override
    public String getMessage(MessageSourceResolvable messageSourceResolvable, Locale locale) throws NoSuchMessageException {
        String s = messageSourceResolvable.getDefaultMessage();
        String result = getMessage(s, messageSourceResolvable.getArguments(), "", locale);
        if (result.isEmpty())
            throw new NoSuchMessageException(s, locale);
        return result;
    }
}
