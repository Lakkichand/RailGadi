package com.railgadi.utilities;

//This Class is to provide timeout information and to determine
//Whether to keep session "alive " or not
public class NetworkParam
{
  //connection timeout in millisecond
  public int mConnectionTimeOut;


  //to determine to keep session alive true or false
  public boolean mKeepSessionAlive;

  //constructor-set boolean variable false by default

  public NetworkParam()

  {
      mKeepSessionAlive = false;
  }
}
