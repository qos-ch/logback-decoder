/**
 * Copyright (C) 2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.decoder;

import org.junit.Before;

/**
 * Base test for verifying field decoders
 *
 * @author Anthony Trinh
 */
public abstract class DecoderTest {
  protected class DecoderBase extends Decoder {}
  protected DecoderBase decoder;

  @Before
  public void setUp() throws Exception {
    decoder = new DecoderBase();
  }
}

