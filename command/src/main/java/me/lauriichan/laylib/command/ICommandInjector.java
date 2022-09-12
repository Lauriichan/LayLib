package me.lauriichan.laylib.command;

public interface ICommandInjector {
    
    void inject(NodeCommand command);
    
    void uninject(NodeCommand command);

}
