/*
 *  Copyright (c) 2011, Franz Bettag <franz@bett.ag>
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BETTAG SYSTEMS UG ''AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL BETTAG SYSTEMS UG BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ag.bett.scala.wowzakka

import com.wowza.wms.application._
import com.wowza.wms.client._
import com.wowza.wms.module._
import com.wowza.wms.request._
import com.wowza.wms.sharedobject._
import com.wowza.wms.stream._
import com.wowza.wms.amf._
import com.wowza.wms.logging._
import com.wowza.util._

import akka.actor._
import akka.dispatch._


class WowzAkkaExampleListener extends WowzAkkaStreamListener


class WowzAkkaExample extends WowzAkkaBase {

  override def onConnect(client: IClient, function: RequestFunction, params: AMFDataList) {
    super.onConnect(client, function, params)
    val c = getClientFromParams(params)

    val future = WowzAkka.actor ? c
    try {
      if (Await.result(future, timeout.duration).asInstanceOf[Boolean]) {
        logger.debug("Authentication successful")
        client.acceptConnection()
      } else {
        logger.debug("Authentication failed")
        client.rejectConnection()
      }
    } catch {
      case _ =>
        logger.error("Authentication failed")
        client.rejectConnection()
    }
  }

  override def onStreamCreate(stream: IMediaStream) {
    stream.addClientListener(new WowzAkkaExampleListener)
  }
 
}



