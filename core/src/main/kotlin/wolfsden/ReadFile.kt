package wolfsden

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule

val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())