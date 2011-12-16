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

import org.apache.log4j.Logger

import akka.actor._
import akka.util._
import akka.util.duration._


object WowzAkka {
  val system = ActorSystem("WowzAkka")
  val actor = system.actorFor("akka://WowzAkka@127.0.0.1:2550/user/serviceA/retrieval")
}


case class WowzAkkaClient(id: String, name: String, platform: String)
case class WowzAkkaClientAuth(id: String, name: String, user: String, pass: String, platform: String)


class WowzAkkaActor extends Actor {

  protected def logger = Logger.getLogger(this.getClass.toString)

  def receive = {
    case "linkup" => logger.debug("running")
    case a: WowzAkkaClientAuth => sender ! true
    case a: ScalaObject => println("unhandled: %s".format(a))
    case _ => println("unhandled")
  }

}


abstract class WowzAkkaBase extends WowModuleBase { //with IModuleOnApp with IModuleOnCall with IModuleOnConnect with IModuleOnStream {

  implicit val timeout = Timeout(10 seconds)
  //implicit val timeout = Timeout(15 millis)

  protected def logger = wowLogger

  protected def getRoom(client: IClient) = client.getAppInstance().getName()

  protected def getClientFromParams(params: AMFDataList): WowzAkkaClientAuth = {
    WowzAkkaClientAuth(
      wowString(params, 3),
      wowString(params, 4),
      wowString(params, 5),
      wowString(params, 6),
      wowString(params, 7))
  }

  protected def getClientFromStream(stream: IMediaStream): WowzAkkaClient = {
    val client = stream.getClient()
    WowzAkkaClient(
      client.getProperties().getPropertyStr("id"),
      client.getProperties().getPropertyStr("name"),
      client.getProperties().getPropertyStr("type"))
  }


  def onAppStart(appInstance: IApplicationInstance) {
    logger.debug("onAppStart")
    WowzAkka.actor ! "linkup"
  }

  def onAppStop(appInstance: IApplicationInstance) {
    logger.debug("onAppStop")
  }

  def onCall(handlerName: String, client: IClient, function: RequestFunction, params: AMFDataList) = {
    logger.debug("function: %s".format(handlerName))
  }

  def onConnect(client: IClient, function: RequestFunction, params: AMFDataList) {
    logger.debug("onConnect for room %s: %s".format(getRoom(client), getClientFromParams(params)))
  }

  def onDisconnect(client: IClient) {
    logger.debug("onDisconnect for room %s".format(getRoom(client)))
  }

  def onConnectAccept(client: IClient) {
    logger.debug("onConnectAccept for room %s".format(getRoom(client)))
  }

  def onConnectReject(client: IClient) {
    logger.debug("onConnectReject for room %s".format(getRoom(client)))
  }

  def onStreamCreate(stream: IMediaStream) {
    logger.debug("onStreamCreate: %s".format(getClientFromStream(stream)))
    //stream.addClientListener(new WowzAkkaStreamListener)
  }
  
  def onStreamDestroy(stream: IMediaStream) {
    logger.debug("onStreamDestroy: %s".format(getClientFromStream(stream)))
  }

  def play(client: IClient, function: RequestFunction, params: AMFDataList) {
    logger.debug("play for room %s: %s".format(getRoom(client), getClientFromParams(params)))
  }

}


class WowzAkkaStreamListener extends IMediaStreamActionNotify2 {

  protected def logger = Logger.getLogger(this.getClass.toString)

  protected def getRoom(client: IClient) = client.getAppInstance().getName()

  protected def getClientFromStream(stream: IMediaStream): WowzAkkaClient = {
    val client = stream.getClient()
    WowzAkkaClient(
      client.getProperties().getPropertyStr("id"),
      client.getProperties().getPropertyStr("name"),
      client.getProperties().getPropertyStr("type"))
  }

  def onPlay(stream: IMediaStream, streamName: String, playStart: Double, playLen: Double, playReset: Int) {
    logger.debug("onPlay: %s".format(getClientFromStream(stream)))
  }

  def onStop(stream: IMediaStream) {
    logger.debug("onStop: %s".format(getClientFromStream(stream)))
  }

  def onPublish(stream: IMediaStream, streamName: String, isRecord: Boolean, isAppend: Boolean) {
    logger.debug("onPublish on %s: %s", streamName, getClientFromStream(stream))
  }

  def onUnPublish(stream: IMediaStream, streamName: String, isRecord: Boolean, isAppend: Boolean) {
    logger.debug("onUnPublish on %s: %s", streamName, getClientFromStream(stream))
  }

  def onPause(stream: IMediaStream, isPause: Boolean, location: Double) {
    logger.debug("onPause: %s".format(getClientFromStream(stream)))
  }

  def onSeek(stream: IMediaStream, location: Double) {
    logger.debug("onSeek: %s".format(getClientFromStream(stream)))
  }

  def onPauseRaw(stream: IMediaStream, isPause: Boolean, location: Double) {
    logger.debug("onPauseRaw: %s".format(getClientFromStream(stream)))
  }

  def onMetaData(stream: IMediaStream, metaDataPAcket: AMFPacket) {
    logger.debug("onMetaData: %s".format(getClientFromStream(stream)))
  }

}


