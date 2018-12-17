package org.san.home.accounts.service.error;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author sanremo16
 */
public class ErrorArgument {

    //    Имя аргумента
    private String argName;

    //    Значение аргумента
    private Object argValue;

    public ErrorArgument(@NotNull String argName, @Nullable Object argValue) {
        this.argName = Objects.requireNonNull(argName);
        this.argValue = argValue;
    }

    public @NotNull
    String getArgName() {
        return argName;
    }

    public void setArgName(@NotNull String argName) {
        this.argName = Objects.requireNonNull(argName);
    }

    public @Nullable
    Object getArgValue() {
        return argValue;
    }

    public void setArgValue(@Nullable Object argValue) {
        this.argValue = argValue;
    }

    @Override
    public int hashCode() {
        return argName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ErrorArgument)
            && Objects.equals(((ErrorArgument) o).argName, this.argName);
    }

    @Override
    public String toString() {
        return argName + " : " + String.valueOf(argValue);
    }
}
