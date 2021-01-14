package ULScene.exceptions;

import java.util.function.Supplier;

public class ULSceneException extends RuntimeException{
    public  ULSceneException(String exceptionMsg){super(exceptionMsg);}
    public  ULSceneException(String exceptionMsg,Exception exception){super(exceptionMsg,exception);}
}
