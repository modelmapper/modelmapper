package org.modelmapper;

public interface ResolveSourceValueInterceptor<S> {

    S use(Object object);

}
