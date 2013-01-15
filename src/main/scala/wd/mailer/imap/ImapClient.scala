package wd.mailer.imap

import scala.Array.canBuildFrom
import com.sun.mail.imap.IMAPFolder
import javax.mail.Folder
import javax.mail.FolderClosedException
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Store
import javax.mail.event.MessageCountAdapter
import javax.mail.event.MessageCountEvent
import wd.emsg.Eaddr
import wd.emsg.EmsgSource
import wd.emsg.NewEmsgListener
import wd.mailer.emsg.JavamailConverters
import wd.util.ThreadUtil
import wd.emsg.Emsg
import wd.util.Props

object ImapClient extends App {
  //Eaddr seems redundant here but it's actually needed to identify the mailbox. 
  //Combination of imapHost and username also uniquely identifies the mailbox but it is not so convenient 
  //In might use "Delivered-To" header as mailbox address but don't want to rely on it as not certain and not secure
  val props = new Props("secret.props")
  val reader = new ImapClient(Eaddr(props("email_address"), props("email_name")), props("imap_server"), props("email_address"), props("email_password"))
  reader.addNewEmsgListener(new NewEmsgListener() {
    override def msgAdded(msgs: Array[Emsg]) {
      println("=================")
      msgs foreach println
    }
  })
}


/**
 * interval in seconds
 */
class ImapClient(mboxEaddr: Eaddr, imapHost: String, username: String, password: String, pollInterval: Int = 60, protocol: String = "imaps")
  extends ImapFolderTree with EmsgSource {

  var session: Option[Session] = None
  var store: Option[Store] = None
  var defaultFolder: Option[Folder] = None
  var emsgListeners: List[NewEmsgListener] = List()

  try {
    val props = System.getProperties

    session = Option(Session.getInstance(props, null))
    //session.get.setDebug(true)

    store = session map (_.getStore(protocol))
    store foreach (_.connect(imapHost, username, password))

    defaultFolder = store map (_.getDefaultFolder)
    val start = System.currentTimeMillis()

    loadFolders
    folderTree foreach { t => println(t.asString()) }

    println("check 1: " + (System.currentTimeMillis() - start))
    folderTree foreach { t => println(t.asString()) }
    println("check 2: " + (System.currentTimeMillis() - start))

    println
    folderList foreach println
    println
    folderRelevantList foreach println

    val msgListener = new ImapMsgListener
    folderRelevantList foreach (_.addMessageCountListener(msgListener))

    folderRelevantList foreach { f =>
      println("Opening folder " + f)
      f.open(Folder.READ_ONLY)
    }
    folderRelevantList foreach (f => ThreadUtil.runBg(enterIdle(f)))

  } catch {
    case ex: Exception => ex.printStackTrace
  }

  private def enterIdle(f: Folder): Unit = {
    println("\nENTER IDLE for folder " + f)
    var supportsIdle = false
    try {
      f match {
        case fold: IMAPFolder =>
          fold.idle
          supportsIdle = true
          println("Supports IDLE")
        case _ =>
      }
    } catch {
      case fex: FolderClosedException => throw fex
      case mex: MessagingException => supportsIdle = false
    }
    while (true) {
      if (supportsIdle && f.isInstanceOf[IMAPFolder]) {
        val fold = f.asInstanceOf[IMAPFolder]
        fold.idle
      } else {
        Thread.sleep(pollInterval * 1000)
        // This is to force the IMAP server to send us EXISTS notifications. 
        f.getMessageCount
      }
    }
  }

  override def addNewEmsgListener(listener: NewEmsgListener) {
    if (!emsgListeners.contains(listener)) {
      emsgListeners ::= listener
    }
  }

  class ImapMsgListener extends MessageCountAdapter {
    override def messagesAdded(ev: MessageCountEvent): Unit = {
      val msgs = ev.getMessages
      println("Got " + msgs.length + " new messages")
      
      val emsgs = for (m <- msgs) yield JavamailConverters.toEmsg(mboxEaddr, m)
      emsgListeners foreach (_.msgAdded(emsgs))
    }
  }
}
/**
ImapClient <-> EmsgStore <-> UI

-ImapClient connects
-ask EmsgStore about folders

use akka futures
notifications as messages
msgstore guard


Emsg addressing: Embox, folder, UID

Don't migrate to scala 2.10 and akka 2.1 and eclipseIDE for 2.10 - use stable releases and proven tools



Minimum features:
* Emsg store with notifications, subscribe for new msgs 
* Synchronize Emsg store with IMAP server
* * get mboxes to synchronize: save mbox config in db. Config mboxes: manually enter email, imapHost, pwd. 
* * Automatically retrieve folders, and mark relevant as active (synchronizable). Allow modifying active flag
* Add new account, allow grouping
* Channel for sorting/filtering rules
* 
Work out best practices for asynchronous communication? Subscribing, subscription period, timeouts/expiration, direct callback vs actor msg

* 
Eliminate duplicates
* 
* 
* 
* 
Agile principles:
* start with something that works end-to-end, no module dev, unit tests when they really needed
* don't overdesign
* working simple software now even if it is ugly, beautify on the way, continuous refactoring
* don't think too much about future features or about being generic
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
* 
 */





