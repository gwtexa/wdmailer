package wd.mailer.emsg

import wd.emsg.Emsg
import javax.mail.Message
import wd.emsg.Eaddr
import javax.mail.UIDFolder
import javax.mail.Address
import javax.mail.internet.InternetAddress


/**
 * Utility methods to convert javamail related objects to Eaddr, Emsg and our other objects.
 * As a result the output objects are not dependent on javamail library.
 */
object JavamailConverters {

  def toEmsg(mboxEaddr: Eaddr, m: Message): Emsg = {
    val uid = m.getFolder match {
      case uf: UIDFolder => uf.getUID(m)
      case _ => throw new Exception("UID not supported in folder " + m.getFolder + " of " + mboxEaddr)
    }
    val text = if (m.getContentType.toUpperCase.startsWith("TEXT/PLAIN")) {
      m.getContent.toString
    } else {
      "ContentType=" + m.getContentType + "\n" + "ContentClass=" + m.getContent.getClass + "\n"
    }
    Emsg(mboxEaddr, m.getFolder.getFullName, uid, toEaddr(m.getFrom()), m.getSubject(), Option(text))
  }
  
  def toEaddr(addr: Address): Eaddr = {
    addr match {
      case ia: InternetAddress => Eaddr(ia.getAddress, ia.getPersonal)
      case a: Address => Eaddr.parse(a.toString)
    }
  }
  
  def toEaddr(addrs: Array[Address]): Eaddr = {
    if (addrs == null) {
      Eaddr()
    } else {
      if (addrs.size > 0) {
        toEaddr(addrs(0))
      } else {
    	Eaddr()
      }
    }
  }
  
  
}