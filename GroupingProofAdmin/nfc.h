/*-
 * Public platform independent Near Field Communication (NFC) library examples
 *
 * Copyright (C) 2009, Roel Verdult
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1) Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  2 )Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Note that this license only applies on the examples, NFC library itself is under LGPL
 *
 */

#ifndef NFC_H
#define NFC_H

#include <stdio.h>
#include <stdlib.h>
#include <stddef.h>
#include <stdint.h>
#include <stdbool.h>
#include <string.h>

#include <math.h>


#include <nfc/nfc.h>

#include <nfc/nfc-messages.h>


#define SAK_FLAG_ATS_SUPPORTED 0x20

#define MAX_FRAME_LEN 264

#define CASCADE_BIT 0x04

typedef struct UID {
	char* uid;
	size_t length;
} UID;

void print_hex (const byte_t * pbtData, const size_t szBytes);
void print_hex_bits (const byte_t * pbtData, const size_t szBits);
static  bool transmit_bits (const byte_t * pbtTx, const size_t szTxBits);
static  bool transmit_bytes (const byte_t * pbtTx, const size_t szTx);
UID* readUID();
void setINFO(UID* uid);

char *base64_encode(const char *data,
                    size_t input_length,
                    size_t *output_length);
char *base64_decode(const char *data,
                    size_t input_length,
                    size_t *output_length);
void build_decoding_table();
void base64_cleanup();

#endif
