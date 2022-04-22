package me.alien.twitch.integration.util;

import net.minecraftforge.event.TickEvent;

import java.awt.event.ActionListener;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class Action {
    Consumer<TickEvent> action;

    public Action(Consumer<TickEvent> action) {
        this.action = action;
    }

    public void callAction(TickEvent e) {
        action.accept(e);
    }
}
