package wd.mailer.imap

import javax.mail.Folder
import wd.struct.MTree

trait ImapFolderTree {

  /**
   * Default folder of the active (connected) IMAP store
   */
  var defaultFolder: Option[Folder]
  
  /**
   * Cached folder tree
   */
  var folderTree: Option[MTree[Folder]] = None
  
  /**
   * Because IMAP IDLE can watch only one mailbox per connection and IMAP servers limit number of concurrent connections
   * from individual IP, we may need to filter folders to monitor only relevant ones. 
   * This is the list of standard folder names commonly used for mailboxes
   */
  val RelevantFolderNames = List("INBOX", "SENT", "SENT MAIL")
  
  /**
   * Flattened tree without root folder
   */
  def folderList: List[Folder] = folderTree.toList flatMap (_.toList)
  
  def folderHoldMsgsList: List[Folder] = folderList filter (f => (Folder.HOLDS_MESSAGES & f.getType) != 0)
  
  def folderRelevantList: List[Folder] = folderHoldMsgsList filter ( f => RelevantFolderNames contains f.getName.toUpperCase )  
  
  /**
   * Read folder structure from the server and save in folderTree 
   */
  def loadFolders: MTree[Folder] = {
    def subtree(f: Folder): MTree[Folder] = MTree(f, f.list.toList map subtree)
    folderTree = defaultFolder map subtree
    if (folderTree.isDefined)
      folderTree.get
    else
      throw new Exception("Could not get load IMAP folder tree")  
  }
  
}