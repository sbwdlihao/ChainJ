package com.lihao.protocol.patricia;

import com.lihao.protocol.bc.Hash;

/**
 * Created by sbwdlihao on 27/12/2016.
 *
 * Leaf describes a key and its corresponding hash of a value inserted into the patricia tree.
 */
class Leaf {

    byte[] key;
    Hash hash;
}
