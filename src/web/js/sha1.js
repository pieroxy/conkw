/* From [js-sha1]{@link https://github.com/emn178/js-sha1} */

var SHA1 = {
  get:function() {
    var HEX_CHARS= '0123456789abcdef'.split('');
    var EXTRA= [-2147483648, 8388608, 32768, 128];
    var SHIFT= [24, 16, 8, 0];

    var res = {
    
      blocks: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],

      h0 : 0x67452301,
      h1 : 0xEFCDAB89,
      h2 : 0x98BADCFE,
      h3 : 0x10325476,
      h4 : 0xC3D2E1F0,
  
      block:0,
      start:0,
      bytes:0,
      hBytes:0,
      finalized:false,
      hashed:false,
      first: true,
      update: function (message) {
        if (this.finalized) {
          return;
        }
        var notString = typeof(message) !== 'string';
        if (notString && message.constructor === root.ArrayBuffer) {
          message = new Uint8Array(message);
        }
        var code, index = 0, i, length = message.length || 0, blocks = this.blocks;
    
        while (index < length) {
          if (this.hashed) {
            this.hashed = false;
            blocks[0] = this.block;
            blocks[16] = blocks[1] = blocks[2] = blocks[3] =
            blocks[4] = blocks[5] = blocks[6] = blocks[7] =
            blocks[8] = blocks[9] = blocks[10] = blocks[11] =
            blocks[12] = blocks[13] = blocks[14] = blocks[15] = 0;
          }
    
          if(notString) {
            for (i = this.start; index < length && i < 64; ++index) {
              blocks[i >> 2] |= message[index] << SHIFT[i++ & 3];
            }
          } else {
            for (i = this.start; index < length && i < 64; ++index) {
              code = message.charCodeAt(index);
              if (code < 0x80) {
                blocks[i >> 2] |= code << SHIFT[i++ & 3];
              } else if (code < 0x800) {
                blocks[i >> 2] |= (0xc0 | (code >> 6)) << SHIFT[i++ & 3];
                blocks[i >> 2] |= (0x80 | (code & 0x3f)) << SHIFT[i++ & 3];
              } else if (code < 0xd800 || code >= 0xe000) {
                blocks[i >> 2] |= (0xe0 | (code >> 12)) << SHIFT[i++ & 3];
                blocks[i >> 2] |= (0x80 | ((code >> 6) & 0x3f)) << SHIFT[i++ & 3];
                blocks[i >> 2] |= (0x80 | (code & 0x3f)) << SHIFT[i++ & 3];
              } else {
                code = 0x10000 + (((code & 0x3ff) << 10) | (message.charCodeAt(++index) & 0x3ff));
                blocks[i >> 2] |= (0xf0 | (code >> 18)) << SHIFT[i++ & 3];
                blocks[i >> 2] |= (0x80 | ((code >> 12) & 0x3f)) << SHIFT[i++ & 3];
                blocks[i >> 2] |= (0x80 | ((code >> 6) & 0x3f)) << SHIFT[i++ & 3];
                blocks[i >> 2] |= (0x80 | (code & 0x3f)) << SHIFT[i++ & 3];
              }
            }
          }
    
          this.lastByteIndex = i;
          this.bytes += i - this.start;
          if (i >= 64) {
            this.block = blocks[16];
            this.start = i - 64;
            this.hash();
            this.hashed = true;
          } else {
            this.start = i;
          }
        }
        if (this.bytes > 4294967295) {
          this.hBytes += this.bytes / 4294967296 << 0;
          this.bytes = this.bytes % 4294967296;
        }
        return this;
      },
      finalize: function () {
        if (this.finalized) {
          return;
        }
        this.finalized = true;
        var blocks = this.blocks, i = this.lastByteIndex;
        blocks[16] = this.block;
        blocks[i >> 2] |= EXTRA[i & 3];
        this.block = blocks[16];
        if (i >= 56) {
          if (!this.hashed) {
            this.hash();
          }
          blocks[0] = this.block;
          blocks[16] = blocks[1] = blocks[2] = blocks[3] =
          blocks[4] = blocks[5] = blocks[6] = blocks[7] =
          blocks[8] = blocks[9] = blocks[10] = blocks[11] =
          blocks[12] = blocks[13] = blocks[14] = blocks[15] = 0;
        }
        blocks[14] = this.hBytes << 3 | this.bytes >>> 29;
        blocks[15] = this.bytes << 3;
        this.hash();
      },
      hash: function () {
        var a = this.h0, b = this.h1, c = this.h2, d = this.h3, e = this.h4;
        var f, j, t, blocks = this.blocks;
    
        for(j = 16; j < 80; ++j) {
          t = blocks[j - 3] ^ blocks[j - 8] ^ blocks[j - 14] ^ blocks[j - 16];
          blocks[j] =  (t << 1) | (t >>> 31);
        }
    
        for(j = 0; j < 20; j += 5) {
          f = (b & c) | ((~b) & d);
          t = (a << 5) | (a >>> 27);
          e = t + f + e + 1518500249 + blocks[j] << 0;
          b = (b << 30) | (b >>> 2);
    
          f = (a & b) | ((~a) & c);
          t = (e << 5) | (e >>> 27);
          d = t + f + d + 1518500249 + blocks[j + 1] << 0;
          a = (a << 30) | (a >>> 2);
    
          f = (e & a) | ((~e) & b);
          t = (d << 5) | (d >>> 27);
          c = t + f + c + 1518500249 + blocks[j + 2] << 0;
          e = (e << 30) | (e >>> 2);
    
          f = (d & e) | ((~d) & a);
          t = (c << 5) | (c >>> 27);
          b = t + f + b + 1518500249 + blocks[j + 3] << 0;
          d = (d << 30) | (d >>> 2);
    
          f = (c & d) | ((~c) & e);
          t = (b << 5) | (b >>> 27);
          a = t + f + a + 1518500249 + blocks[j + 4] << 0;
          c = (c << 30) | (c >>> 2);
        }
    
        for(; j < 40; j += 5) {
          f = b ^ c ^ d;
          t = (a << 5) | (a >>> 27);
          e = t + f + e + 1859775393 + blocks[j] << 0;
          b = (b << 30) | (b >>> 2);
    
          f = a ^ b ^ c;
          t = (e << 5) | (e >>> 27);
          d = t + f + d + 1859775393 + blocks[j + 1] << 0;
          a = (a << 30) | (a >>> 2);
    
          f = e ^ a ^ b;
          t = (d << 5) | (d >>> 27);
          c = t + f + c + 1859775393 + blocks[j + 2] << 0;
          e = (e << 30) | (e >>> 2);
    
          f = d ^ e ^ a;
          t = (c << 5) | (c >>> 27);
          b = t + f + b + 1859775393 + blocks[j + 3] << 0;
          d = (d << 30) | (d >>> 2);
    
          f = c ^ d ^ e;
          t = (b << 5) | (b >>> 27);
          a = t + f + a + 1859775393 + blocks[j + 4] << 0;
          c = (c << 30) | (c >>> 2);
        }
    
        for(; j < 60; j += 5) {
          f = (b & c) | (b & d) | (c & d);
          t = (a << 5) | (a >>> 27);
          e = t + f + e - 1894007588 + blocks[j] << 0;
          b = (b << 30) | (b >>> 2);
    
          f = (a & b) | (a & c) | (b & c);
          t = (e << 5) | (e >>> 27);
          d = t + f + d - 1894007588 + blocks[j + 1] << 0;
          a = (a << 30) | (a >>> 2);
    
          f = (e & a) | (e & b) | (a & b);
          t = (d << 5) | (d >>> 27);
          c = t + f + c - 1894007588 + blocks[j + 2] << 0;
          e = (e << 30) | (e >>> 2);
    
          f = (d & e) | (d & a) | (e & a);
          t = (c << 5) | (c >>> 27);
          b = t + f + b - 1894007588 + blocks[j + 3] << 0;
          d = (d << 30) | (d >>> 2);
    
          f = (c & d) | (c & e) | (d & e);
          t = (b << 5) | (b >>> 27);
          a = t + f + a - 1894007588 + blocks[j + 4] << 0;
          c = (c << 30) | (c >>> 2);
        }
    
        for(; j < 80; j += 5) {
          f = b ^ c ^ d;
          t = (a << 5) | (a >>> 27);
          e = t + f + e - 899497514 + blocks[j] << 0;
          b = (b << 30) | (b >>> 2);
    
          f = a ^ b ^ c;
          t = (e << 5) | (e >>> 27);
          d = t + f + d - 899497514 + blocks[j + 1] << 0;
          a = (a << 30) | (a >>> 2);
    
          f = e ^ a ^ b;
          t = (d << 5) | (d >>> 27);
          c = t + f + c - 899497514 + blocks[j + 2] << 0;
          e = (e << 30) | (e >>> 2);
    
          f = d ^ e ^ a;
          t = (c << 5) | (c >>> 27);
          b = t + f + b - 899497514 + blocks[j + 3] << 0;
          d = (d << 30) | (d >>> 2);
    
          f = c ^ d ^ e;
          t = (b << 5) | (b >>> 27);
          a = t + f + a - 899497514 + blocks[j + 4] << 0;
          c = (c << 30) | (c >>> 2);
        }
    
        this.h0 = this.h0 + a << 0;
        this.h1 = this.h1 + b << 0;
        this.h2 = this.h2 + c << 0;
        this.h3 = this.h3 + d << 0;
        this.h4 = this.h4 + e << 0;
      },
      hex: function () {
        this.finalize();
    
        var h0 = this.h0, h1 = this.h1, h2 = this.h2, h3 = this.h3, h4 = this.h4;
    
        return HEX_CHARS[(h0 >> 28) & 0x0F] + HEX_CHARS[(h0 >> 24) & 0x0F] +
               HEX_CHARS[(h0 >> 20) & 0x0F] + HEX_CHARS[(h0 >> 16) & 0x0F] +
               HEX_CHARS[(h0 >> 12) & 0x0F] + HEX_CHARS[(h0 >> 8) & 0x0F] +
               HEX_CHARS[(h0 >> 4) & 0x0F] + HEX_CHARS[h0 & 0x0F] +
               HEX_CHARS[(h1 >> 28) & 0x0F] + HEX_CHARS[(h1 >> 24) & 0x0F] +
               HEX_CHARS[(h1 >> 20) & 0x0F] + HEX_CHARS[(h1 >> 16) & 0x0F] +
               HEX_CHARS[(h1 >> 12) & 0x0F] + HEX_CHARS[(h1 >> 8) & 0x0F] +
               HEX_CHARS[(h1 >> 4) & 0x0F] + HEX_CHARS[h1 & 0x0F] +
               HEX_CHARS[(h2 >> 28) & 0x0F] + HEX_CHARS[(h2 >> 24) & 0x0F] +
               HEX_CHARS[(h2 >> 20) & 0x0F] + HEX_CHARS[(h2 >> 16) & 0x0F] +
               HEX_CHARS[(h2 >> 12) & 0x0F] + HEX_CHARS[(h2 >> 8) & 0x0F] +
               HEX_CHARS[(h2 >> 4) & 0x0F] + HEX_CHARS[h2 & 0x0F] +
               HEX_CHARS[(h3 >> 28) & 0x0F] + HEX_CHARS[(h3 >> 24) & 0x0F] +
               HEX_CHARS[(h3 >> 20) & 0x0F] + HEX_CHARS[(h3 >> 16) & 0x0F] +
               HEX_CHARS[(h3 >> 12) & 0x0F] + HEX_CHARS[(h3 >> 8) & 0x0F] +
               HEX_CHARS[(h3 >> 4) & 0x0F] + HEX_CHARS[h3 & 0x0F] +
               HEX_CHARS[(h4 >> 28) & 0x0F] + HEX_CHARS[(h4 >> 24) & 0x0F] +
               HEX_CHARS[(h4 >> 20) & 0x0F] + HEX_CHARS[(h4 >> 16) & 0x0F] +
               HEX_CHARS[(h4 >> 12) & 0x0F] + HEX_CHARS[(h4 >> 8) & 0x0F] +
               HEX_CHARS[(h4 >> 4) & 0x0F] + HEX_CHARS[h4 & 0x0F];
      },
      digest: function () {
        this.finalize();
    
        var h0 = this.h0, h1 = this.h1, h2 = this.h2, h3 = this.h3, h4 = this.h4;
    
        return [
          (h0 >> 24) & 0xFF, (h0 >> 16) & 0xFF, (h0 >> 8) & 0xFF, h0 & 0xFF,
          (h1 >> 24) & 0xFF, (h1 >> 16) & 0xFF, (h1 >> 8) & 0xFF, h1 & 0xFF,
          (h2 >> 24) & 0xFF, (h2 >> 16) & 0xFF, (h2 >> 8) & 0xFF, h2 & 0xFF,
          (h3 >> 24) & 0xFF, (h3 >> 16) & 0xFF, (h3 >> 8) & 0xFF, h3 & 0xFF,
          (h4 >> 24) & 0xFF, (h4 >> 16) & 0xFF, (h4 >> 8) & 0xFF, h4 & 0xFF
        ];
      },
      arrayBuffer: function () {
        this.finalize();
    
        var buffer = new ArrayBuffer(20);
        var dataView = new DataView(buffer);
        dataView.setUint32(0, this.h0);
        dataView.setUint32(4, this.h1);
        dataView.setUint32(8, this.h2);
        dataView.setUint32(12, this.h3);
        dataView.setUint32(16, this.h4);
        return buffer;
      }
    };
    res.toString = res.hex;
    return res;
  }
}