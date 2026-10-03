package com.family_tree.serialized_structures;

import com.family_tree.math_classes.Geometry.*;
import com.family_tree.util.*;

import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import java.io.File;


public record AppConfig (
	Vector2 win_res,
	Vector2 block_size
) {}
