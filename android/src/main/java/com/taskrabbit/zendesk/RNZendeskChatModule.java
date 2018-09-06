package com.taskrabbit.zendesk;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.zopim.android.sdk.api.Chat;
import com.zopim.android.sdk.api.ChatService;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.api.ZopimChatApi;
import com.zopim.android.sdk.data.observers.ChatItemsObserver;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.model.items.AgentMessage;
import com.zopim.android.sdk.model.items.ChatEvent;
import com.zopim.android.sdk.model.items.RowItem;
import com.zopim.android.sdk.prechat.ChatListener;
import com.zopim.android.sdk.prechat.ZopimChatActivity;
import com.zopim.android.sdk.prechat.ZopimPreChatFragment;

import java.lang.String;
import java.util.Map;
import java.util.TreeMap;

public class RNZendeskChatModule extends ReactContextBaseJavaModule {
    private ReactContext mReactContext;

    public RNZendeskChatModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        ZopimChatApi.getDataSource().addChatLogObserver(chatItemsObserver);
    }

    @Override
    public String getName() {
        return "RNZendeskChatModule";
    }

    @ReactMethod
    public void setVisitorInfo(ReadableMap options) {
        VisitorInfo.Builder builder = new VisitorInfo.Builder();
        
        if (options.hasKey("name")) {
            builder.name(options.getString("name"));
        } else {
            builder.name(null);
        }

        if (options.hasKey("email")) {
            builder.email(options.getString("email"));
        } else {
            builder.email(null);
        }

        if (options.hasKey("phone")) {
            builder.phoneNumber(options.getString("phone"));
        } else {
            builder.phoneNumber(null);
        }

        if (options.hasKey("note")) {
            builder.note(options.getString("note"));
        }

        VisitorInfo visitorData = builder.build();
        ZopimChat.setVisitorInfo(visitorData);
    }

    @ReactMethod
    public void init(String key) {
        ZopimChat.init(key);
    }

    @ReactMethod
    public void startChat(ReadableMap options) {
        setVisitorInfo(options);
        Activity activity = getCurrentActivity();
        if (activity != null) {
            activity.startActivity(new Intent(mReactContext, ZopimChatActivity.class));
        }
    }

    @ReactMethod
    public void unreadMessagesCount(Promise promise) {
        // Not sure how to get the unread count yet...
        promise.resolve(0);
    }

    @ReactMethod
    public void setNote(String note) {
        // setNote not implemented in Android because of limitations
        // of the Chat SDK. Use note in `startChat` instead (but note
        // that it will only append notes, not overwrite them).
    }

    private ChatItemsObserver chatItemsObserver = new ChatItemsObserver(mReactContext) {
        @Override
        protected void updateChatItems(TreeMap<String, RowItem> treeMap) {
            // Check if the Zendesk chat activity is currently in the foreground. If that is
            // the case we
            // do nothing, as we don't want to send notifications while the chat is active.
            ActivityManager am = (ActivityManager) mReactContext.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            boolean chatIsVisible = cn.toShortString().contains("ZopimChatActivity");
            if (chatIsVisible)
                return;

            Map.Entry<String, RowItem> lastEntry = treeMap.lastEntry();
            if (lastEntry == null)
                return;

            RowItem item = lastEntry.getValue();

            if (item == null) {
                return;
            }

            WritableMap payload = Arguments.createMap();
            payload.putString("eventId", item.getId());
            payload.putString("timestamp", item.getTimestamp().toString());
            payload.putString("nickname", item.getParticipantId());
            payload.putString("displayName", item.getDisplayName());

            String type;
            switch (item.getType()) {
            case AGENT_ATTACHMENT:
                type = "AgentUpload";
                break;
            case AGENT_MESSAGE:
                type = "AgentMessage";
                break;
            default:
                type = "Unknown";
            }

            payload.putString("type", type);

            if (item.getType() == RowItem.Type.AGENT_MESSAGE) {
                AgentMessage msg = (AgentMessage) item;
                payload.putString("message", msg.getMessage());
            }

            mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("ChatLogEvent",
                    payload);
        }
    };
}
