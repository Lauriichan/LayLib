package me.lauriichan.laylib.localization;

public final class PlaceholderProcessor implements IMessageProcessor {

    public static final PlaceholderProcessor INSTANCE = new PlaceholderProcessor();

    private PlaceholderProcessor() {}

    @Override
    public String process(MessageProcess process, String message) {
        StringBuilder output = new StringBuilder(), builder = null;
        char ch, nch;
        int length = message.length();
        for (int i = 0; i < length; i++) {
            ch = message.charAt(i);
            if (ch == '\\' && i + 1 < length) {
                nch = message.charAt(i + 1);
                if (nch == '$') {
                    output.append(nch);
                    continue;
                }
            }
            if (ch == '$' && i + 1 < length) {
                if (builder != null) {
                    processPlaceholder(process, output, builder.substring(1));
                }
                builder = new StringBuilder().append(ch);
                continue;
            }
            if (builder == null) {
                output.append(ch);
                continue;
            }
            if (Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '.' || ch == '-' || ch == '_' || ch == '#') {
                builder.append(ch);
                continue;
            }
            processPlaceholder(process, output, builder.substring(1));
            builder = null;
            output.append(ch);
        }
        return output.toString();
    }

    private void processPlaceholder(MessageProcess process, StringBuilder output, String id) {
        if (id.startsWith("#")) {
            String message = process.translateMessage(id.substring(1));
            if (message == null) {
                return;
            }
            output.append(message);
            return;
        }
        String content = process.get(id);
        if (content == null) {
            return;
        }
        if (content.equals("null")) {
            output.append(content);
            return;
        }
        output.append(process.process(content));
    }

}
