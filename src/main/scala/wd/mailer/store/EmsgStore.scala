package wd.mailer.store

import javax.mail.Message



/**
 * API for storing messages in MongoDB. Make a trait to provide db independent API
 */
trait EmsgStore {

  def insert(emsg: Message)
  
  def select()
  
  
}