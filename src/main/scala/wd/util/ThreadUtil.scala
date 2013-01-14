package wd.util

object ThreadUtil {

  def runBg(code: => Unit) {
    new Thread { override def run { code } }.start
  }
  
}