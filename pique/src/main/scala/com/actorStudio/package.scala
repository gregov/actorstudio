package com.actorStudio

/**
  * Created by greg on 2016-10-03.
  * TODO: transform this into a service to be injected in each actor
  */
trait RandomBehaviour {
  var minTime = 0
  var maxTime = 60
  var crashRate = .1

  def rollTheDice(): Unit ={
    val rnd = new scala.util.Random
    val range = minTime to maxTime
    val pause = minTime + rnd.nextInt(maxTime - minTime)
    Thread.sleep(pause)

    if (rnd.nextFloat() < crashRate) {
      throw new Exception("Oops")
    }
  }
}
