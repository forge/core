/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package pl.silvermedia.ws;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface ContactUsService {
  List<Message> getMessages();
  Message getFirstMessage();
    void postMessage(@WebParam(name = "message") Message message);
}
