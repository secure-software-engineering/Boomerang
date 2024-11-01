package boomerang.scene.opal

import boomerang.scene.{DeclaredMethod, InvokeExpr, Val}
import org.opalj.tac.{DUVar, FunctionCall, InstanceFunctionCall, InstanceMethodCall, MethodCall, NonVirtualFunctionCall, NonVirtualMethodCall}
import org.opalj.value.ValueInformation

import java.util

class OpalMethodInvokeExpr(val delegate: MethodCall[DUVar[ValueInformation]], method: OpalMethod) extends InvokeExpr {

  override def getArg(index: Int): Val = getArgs.get(index)

  override def getArgs: util.List[Val] = {
    val result = new util.ArrayList[Val]

    delegate.params.foreach(param => {
      result.add(new OpalLocal(param, method))
    })

    result
  }

  override def isInstanceInvokeExpr: Boolean = delegate.isInstanceOf[InstanceMethodCall[_]]

  override def getBase: Val = {
    if (isInstanceInvokeExpr) {
      return new OpalLocal(delegate.asInstanceMethodCall.receiver, method)
    }

    throw new RuntimeException("Method call is not an instance invoke expression")
  }

  override def getMethod: DeclaredMethod = {
    val resolvedMethod = OpalClient.resolveMethodRef(delegate.declaringClass, delegate.name, delegate.descriptor)

    if (resolvedMethod.isDefined) {
      OpalDeclaredMethod(this, resolvedMethod.get)
    } else {
      OpalPhantomDeclaredMethod(this, delegate.declaringClass, delegate.name, delegate.descriptor, delegate.isStaticMethodCall)
    }
  }

  override def isSpecialInvokeExpr: Boolean = delegate.astID == NonVirtualMethodCall.ASTID

  override def isStaticInvokeExpr: Boolean = delegate.isStaticMethodCall

  override def hashCode(): Int = 31 + delegate.hashCode()

  private def canEqual(a: Any): Boolean = a.isInstanceOf[OpalMethodInvokeExpr]

  override def equals(obj: Any): Boolean = obj match {
    case other: OpalMethodInvokeExpr => other.canEqual(this) && this.delegate == other.delegate
    case _ => false
  }

  override def toString: String = delegate.toString
}

class OpalFunctionInvokeExpr(val delegate: FunctionCall[DUVar[ValueInformation]], method: OpalMethod) extends InvokeExpr {

  override def getArg(index: Int): Val = getArgs.get(index)

  override def getArgs: util.List[Val] = {
    val result = new util.ArrayList[Val]

    delegate.params.foreach(param => {
      result.add(new OpalLocal(param, method))
    })

    result
  }

  override def isInstanceInvokeExpr: Boolean = delegate.isInstanceOf[InstanceFunctionCall[_]]

  override def getBase: Val = {
    if (isInstanceInvokeExpr) {
      return new OpalLocal(delegate.asInstanceFunctionCall.receiver, method)
    }

    throw new RuntimeException("Function call is not an instance invoke expression")
  }

  override def getMethod: DeclaredMethod = {
    val resolvedMethod = OpalClient.resolveMethodRef(delegate.declaringClass, delegate.name, delegate.descriptor)

    if (resolvedMethod.isDefined) {
      OpalDeclaredMethod(this, resolvedMethod.get)
    } else {
      OpalPhantomDeclaredMethod(this, delegate.declaringClass, delegate.name, delegate.descriptor, delegate.isStaticFunctionCall)
    }
  }

  override def isSpecialInvokeExpr: Boolean = delegate.astID == NonVirtualFunctionCall.ASTID

  override def isStaticInvokeExpr: Boolean = delegate.isStaticFunctionCall

  override def hashCode(): Int = 31 + delegate.hashCode()

  private def canEqual(a: Any): Boolean = a.isInstanceOf[OpalFunctionInvokeExpr]

  override def equals(obj: Any): Boolean = obj match {
    case other: OpalFunctionInvokeExpr => other.canEqual(this) && this.delegate == other.delegate
    case _ => false
  }

  override def toString: String = delegate.toString
}
