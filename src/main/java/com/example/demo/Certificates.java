/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package com.example.demo;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.List;

public class Certificates {
  private static final String X509_CERTIFICATE_TYPE = "X.509";

  private static final String CERTIFICATION_CHAIN_ENCODING = "PkiPath";
  
  private static CertificateFactory FACTORY;
  
  private static CertificateFactory getFactory() throws CertificateException {
    return FACTORY == null ? FACTORY = CertificateFactory.getInstance(X509_CERTIFICATE_TYPE) : FACTORY;
  }
  
  private Certificates() {}
  
  public static X509Certificate create(InputStream is) throws CertificateException {
    //Args.requireNonNull(is, "inputstream is null");
    return (X509Certificate)getFactory().generateCertificate(is);
  }
  
  public static byte[] toByteArray(final List<Certificate> chain) throws CertificateException {
    //Args.requireNonEmpty(chain, "chain is empty");
    return getFactory().generateCertPath(chain).getEncoded(CERTIFICATION_CHAIN_ENCODING);
  }


}
