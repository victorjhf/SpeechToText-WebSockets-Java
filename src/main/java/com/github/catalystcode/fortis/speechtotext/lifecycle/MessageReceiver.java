package com.github.catalystcode.fortis.speechtotext.lifecycle;

import com.github.catalystcode.fortis.speechtotext.utils.Func;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.Map;

import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageHeaders.PATH;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageFields.DISPLAY_TEXT;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageFields.RECOGNITION_STATUS;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServiceMessageFields.SUCCESS_STATUS;
import static com.github.catalystcode.fortis.speechtotext.constants.SpeechServicePaths.SPEECH_PHRASE;
import static com.github.catalystcode.fortis.speechtotext.utils.MessageUtils.parseBody;
import static com.github.catalystcode.fortis.speechtotext.utils.MessageUtils.parseHeaders;


public class MessageReceiver {
    private static final Logger log = Logger.getLogger(MessageReceiver.class);
    private final Func<String> onResult;

    public MessageReceiver(Func<String> onResult) {
        this.onResult = onResult;
    }

    public void onMessage(String message) {
        Map<String, String> headers = parseHeaders(message);
        JSONObject body = parseBody(message);

        String path = headers.get(PATH);
        log.info("Got message at path " + path + " with payload '" + body + "'");

        if (SPEECH_PHRASE.equalsIgnoreCase(path)) {
            onSpeechPhrase(body);
        } else {
            log.warn("Unhandled message at path " + path);
        }
    }

    private void onSpeechPhrase(JSONObject message) {
        String status = message.getString(RECOGNITION_STATUS);

        if (!SUCCESS_STATUS.equalsIgnoreCase(status)) {
            log.warn("Unable to recognize audio: " + message);
            return;
        }

        String displayText = message.getString(DISPLAY_TEXT);
        onResult.call(displayText);
    }
}