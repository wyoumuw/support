package com.youmu.common.wrapper;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * @Author: YOUMU
 * @Description:代理修改equals,toString和hashCode方法
 * @Date: 2017/12/25
 */
public class ObjectMapWrapper<T> {

    private ToIntFunction<T> hashCodeFunction = Object::hashCode;
    private BiPredicate<T, Object> equalsFunction = Object::equals;
    private Function<T, String> toStringFunction = Object::toString;

    private T target;

    public ObjectMapWrapper(ToIntFunction<T> hashCodeFunction,
            BiPredicate<T, Object> equalsFunction, Function<T, String> toStringFunction, T target) {
        this.hashCodeFunction = hashCodeFunction;
        this.equalsFunction = equalsFunction;
        this.toStringFunction = toStringFunction;
        this.target = Optional.of(target).get();
    }

    @Override
    public int hashCode() {
        return Optional.ofNullable(hashCodeFunction).orElse(Object::hashCode).applyAsInt(target);
    }

    @Override
    public boolean equals(Object obj) {
        return Optional.ofNullable(equalsFunction).orElse(Object::equals).test(target, obj);
    }

    @Override
    public String toString() {
        return Optional.ofNullable(toStringFunction).orElse(Object::toString).apply(target);
    }

    public T getTarget() {
        return target;
    }

    public static class Builder<T> implements com.youmu.common.Builder<ObjectMapWrapper<T>> {
        private ToIntFunction<T> hashCodeFunction = Object::hashCode;
        private BiPredicate<T, Object> equalsFunction = Object::equals;
        private Function<T, String> toStringFunction = Object::toString;
        private T target;

        public Builder(T target) {
            this.target = target;
        }

        public ToIntFunction<T> getHashCodeFunction() {
            return hashCodeFunction;
        }

        public Builder<T> setHashCodeFunction(ToIntFunction<T> hashCodeFunction) {
            this.hashCodeFunction = hashCodeFunction;
            return this;
        }

        public BiPredicate<T, Object> getEqualsFunction() {
            return equalsFunction;
        }

        public Builder<T> setEqualsFunction(BiPredicate<T, Object> equalsFunction) {
            this.equalsFunction = equalsFunction;
            return this;
        }

        public Function<T, String> getToStringFunction() {
            return toStringFunction;
        }

        public Builder<T> setToStringFunction(Function<T, String> toStringFunction) {
            this.toStringFunction = toStringFunction;
            return this;
        }

        public T getTarget() {
            return target;
        }

        @Override
        public ObjectMapWrapper<T> build() {
            return new ObjectMapWrapper(this.hashCodeFunction, this.equalsFunction,
                    this.toStringFunction, this.target);
        }
    }
}
