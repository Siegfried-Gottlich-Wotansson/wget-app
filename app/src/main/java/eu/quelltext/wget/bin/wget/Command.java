package eu.quelltext.wget.bin.wget;

import android.content.Context;
import android.graphics.Path;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import eu.quelltext.wget.activities.MainActivity;
import eu.quelltext.wget.bin.BinaryAccess;
import eu.quelltext.wget.bin.Executable;

public class Command implements Parcelable {

    public static final Command VERSION = new Command().addOption(BinaryOption.VERSION);
    private static final String EXAMPLE_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/3/39/Official_gnu.svg";
    public static final Command GET_IMAGE = new Command().addOption(BinaryOption.CONTINUE).addUrl(EXAMPLE_IMAGE_URL);

    private Command addUrl(String url) {
        urls.add(url);
        return this;
    }

    private Command addOption(Option option) {
        options.add(option);
        return this;
    }

    private final List<Option> options = new ArrayList<>();
    private final List<String> urls = new ArrayList<>();

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(options.size());
        for (Option option: options) {
            parcel.writeParcelable(option, i);
        }
        parcel.writeInt(urls.size());
        for (String url: urls) {
            parcel.writeString(url);
        }
    }

    public Command(Parcel in) {
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            // load inner parcelable class, see
            // https://www.survivingwithandroid.com/android-parcelable-tutorial-list-class-2/
            options.add((Option)in.readParcelable(Option.class.getClassLoader()));
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            urls.add(in.readString());
        }
    }

    public Command() {
    }

    public static final Creator<Command> CREATOR = new Creator<Command>() {
        @Override
        public Command createFromParcel(Parcel in) {
            return new Command(in);
        }

        @Override
        public Command[] newArray(int size) {
            return new Command[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public Executable.Result run(Context context) throws IOException {
        IWget wget = new BinaryAccess(context).wget();
        List<String> command = new ArrayList<>();
        for (Option option: options) {
            for (String argument : option.asArguments()) {
                command.add(argument);
            }
        }
        String[] commandList = new String[command.size()];
        for (int i = 0; i < command.size(); i++) {
            commandList[i] = command.get(i);
        }
        return wget.run(commandList);
    }

    public List<String> getUrls() {
        return urls;
    }

    public List<Option> getOptions() {
        return options;
    }

    public String getOptionsText(Context context) {
        if (options.size() == 0) {
            return "";
        }
        String result = "";
        String delimiter = ", ";
        for (Option option: options) {
            result += option.toShortText(context);
            result += delimiter;
        }
        return result.substring(0, result.length() - delimiter.length());
    }

    public String getUrlText() {
        if (options.size() == 0) {
            return "";
        }
        String result = "";
        String delimiter = "\n";
        for (String url: urls) {
            result += url + delimiter;
        }
        return result.substring(0, result.length() - delimiter.length());
    }
}