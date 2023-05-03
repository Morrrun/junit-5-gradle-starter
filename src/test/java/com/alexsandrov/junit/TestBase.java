package com.alexsandrov.junit;

import com.alexsandrov.junit.extension.ConditionalExtension;
import com.alexsandrov.junit.extension.GlobalExtension;
import com.alexsandrov.junit.extension.PostProcessingExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
        GlobalExtension.class,
        PostProcessingExtension.class,
        ConditionalExtension.class
})
public abstract class TestBase {
}
