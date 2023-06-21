package jda.modules.msacommon.controller;

import java.util.Collection;

public abstract class GetByAssociation <V, T>{
	abstract void execute(V propertyValue, String propertyName, Collection<T> records);
}
