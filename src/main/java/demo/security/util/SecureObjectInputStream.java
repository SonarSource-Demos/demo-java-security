package demo.security.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SecureObjectInputStream extends ObjectInputStream {
    private final Set<String> approvedClasses;

    public SecureObjectInputStream(InputStream in, Class<?>... approvedClasses) throws IOException {
        super(in);
        Set<String> names = new HashSet<>();
        Arrays.stream(approvedClasses).forEach(c -> names.add(c.getName()));
        this.approvedClasses = Collections.unmodifiableSet(names);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
        if (!approvedClasses.contains(osc.getName())) {
            throw new InvalidClassException("Unauthorized deserialization", osc.getName());
        }
        return super.resolveClass(osc);
    }
}
