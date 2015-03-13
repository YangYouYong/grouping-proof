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

#include "nfc.h"

static char encoding_table[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                                'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                                'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                                'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
                                'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                                'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
                                'w', 'x', 'y', 'z', '0', '1', '2', '3',
                                '4', '5', '6', '7', '8', '9', '+', '/'};
static char *decoding_table = NULL;
static int mod_table[] = {0, 2, 1};


static byte_t abtRx[MAX_FRAME_LEN];
static size_t szRxBits;
static size_t szRx;
static byte_t abtRawUid[8];
static byte_t abtAtqa[2];
static byte_t abtSak;
static size_t szCL = 1;//Always start with Cascade Level 1 (CL1)
static nfc_device_t *pnd;

bool    quiet_output = false;

// ISO14443A Anti-Collision Commands
byte_t  abtReqa[1] = { 0x26 };
byte_t  abtSelectAll[2] = { 0x93, 0x20 };
byte_t  abtSelectAll2[2] = { 0x95, 0x20 };
byte_t  abtSelectTag[9] = { 0x93, 0x70, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
byte_t  abtSelectTag2[9] = { 0x95, 0x70, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

// Commands
byte_t  write[8] = { 0x0B, 0x00, 0xB0, 0x00, 0x00, 0x00, 0x00, 0x00, };

byte_t  abtRats[4] = { 0xe0, 0x50, 0x00, 0x00 };
byte_t  abtHalt[4] = { 0x50, 0x00, 0x00, 0x00 };


void print_hex(const byte_t * pbtData, const size_t szBytes) {
	size_t szPos;

	for (szPos = 0; szPos < szBytes; szPos++) {
		printf("%02x  ", pbtData[szPos]);
	}
	printf("\n");
}

void print_hex_bits(const byte_t * pbtData, const size_t szBits) {
	uint8_t uRemainder;
	size_t szPos;
	size_t szBytes = szBits / 8;

	for (szPos = 0; szPos < szBytes; szPos++) {
		printf("%02x  ", pbtData[szPos]);
	}

	uRemainder = szBits % 8;
	// Print the rest bits
	if (uRemainder != 0) {
		if (uRemainder < 5)
			printf("%01x (%d bits)", pbtData[szBytes], uRemainder);
		else
			printf("%02x (%d bits)", pbtData[szBytes], uRemainder);
	}
	printf("\n");
}

static bool transmit_bits(const byte_t * pbtTx, const size_t szTxBits) {
	// Show transmitted command
	if (!quiet_output) {
		printf("Sent bits:     ");
		print_hex_bits(pbtTx, szTxBits);
	}
	// Transmit the bit frame command, we don't use the arbitrary parity feature
	if (!nfc_initiator_transceive_bits(pnd, pbtTx, szTxBits, NULL, abtRx,
			&szRxBits, NULL))
		;
	//return false;

	// Show received answer
	if (!quiet_output) {
		printf("Received bits: ");
		print_hex_bits(abtRx, szRxBits);
	}
	// Succesful transfer
	return true;
}

static bool transmit_bytes(const byte_t * pbtTx, const size_t szTx) {
	// Show transmitted command
	if (!quiet_output) {
		printf("Sent bits:     ");
		print_hex(pbtTx, szTx);
	}
	// Transmit the command bytes
	if (!nfc_initiator_transceive_bytes(pnd, pbtTx, szTx, abtRx, &szRx))
		;
	//return false;

	// Show received answer
	if (!quiet_output) {
		printf("Received bits: ");
		print_hex(abtRx, szRx);
	}
	// Succesful transfer
	return true;
}

UID* readUID() {

	// Try to open the NFC reader
	pnd = nfc_connect(NULL);

	while (!pnd) {
		pnd = nfc_connect(NULL);
	}

	// Initialise NFC device as "initiator"
	nfc_initiator_init(pnd);

	// Drop the field for a while
	if (!nfc_configure(pnd, NDO_ACTIVATE_FIELD, false)) {
		nfc_perror(pnd, "nfc_configure");
		exit(EXIT_FAILURE);
	}

	// Configure the CRC
	if (!nfc_configure(pnd, NDO_HANDLE_CRC, false)) {
		nfc_perror(pnd, "nfc_configure");
		exit(EXIT_FAILURE);
	}
	// Configure parity settings
	if (!nfc_configure(pnd, NDO_HANDLE_PARITY, true)) {
		nfc_perror(pnd, "nfc_configure");
		exit(EXIT_FAILURE);
	}
	// Use raw send/receive methods
	if (!nfc_configure(pnd, NDO_EASY_FRAMING, false)) {
		nfc_perror(pnd, "nfc_configure");
		exit(EXIT_FAILURE);
	}
	// Disable 14443-4 autoswitching
	if (!nfc_configure(pnd, NDO_AUTO_ISO14443_4, false)) {
		nfc_perror(pnd, "nfc_configure");
		exit(EXIT_FAILURE);
	}
	// Force 14443-A mode
	if (!nfc_configure(pnd, NDO_FORCE_ISO14443_A, true)) {
		nfc_perror(pnd, "nfc_configure");
		exit(EXIT_FAILURE);
	}

	// Enable field so more power consuming cards can power themselves up
	if (!nfc_configure(pnd, NDO_ACTIVATE_FIELD, true)) {
		nfc_perror(pnd, "nfc_configure");
		exit(EXIT_FAILURE);
	}

	printf("Connected to NFC reader: %s\n\n", pnd->acName);

	// Send the 7 bits request command specified in ISO 14443A (0x26)
	if (!transmit_bits(abtReqa, 7)) { // Check for ultralight
		printf("Error: No tag available\n");
		nfc_disconnect(pnd);
		return 0;
	}
	memcpy(abtAtqa, abtRx, 2);

	transmit_bytes(abtSelectAll, 2);

	memcpy (abtRawUid, abtRx, 4);

    memcpy (abtSelectTag + 2, abtRx, 5);
	iso14443a_crc_append(abtSelectTag, 7);
	transmit_bytes(abtSelectTag, 9);

	transmit_bytes(abtSelectAll2, 2);

	memcpy (abtRawUid+4, abtRx, 4);
    memcpy (abtSelectTag2 + 2, abtRx, 5);
	iso14443a_crc_append(abtSelectTag2, 7);
	transmit_bytes(abtSelectTag2, 9);

	nfc_disconnect(pnd);

	UID* uid = malloc(sizeof(UID));
	uid->length = 8;
	memcpy (uid->uid, abtRawUid, 8);
	return uid;
}

void setINFO(UID* uid) {

	// Try to open the NFC reader
	pnd = nfc_connect(NULL);

	while (!pnd) {
		pnd = nfc_connect(NULL);
	}

	// Initialise NFC device as "initiator"
	nfc_initiator_init(pnd);

	// Drop the field for a while
	if (!nfc_configure(pnd, NDO_ACTIVATE_FIELD, false)) {
		nfc_perror(pnd, "nfc_configure");
		exit(EXIT_FAILURE);
	}

	// Configure the CRC
	if (!nfc_configure(pnd, NDO_HANDLE_CRC, false)) {
		nfc_perror(pnd, "nfc_configure");
		exit(EXIT_FAILURE);
	}
	// Configure parity settings
	if (!nfc_configure(pnd, NDO_HANDLE_PARITY, true)) {
		nfc_perror(pnd, "nfc_configure");
		exit(EXIT_FAILURE);
	}
	// Use raw send/receive methods
	if (!nfc_configure(pnd, NDO_EASY_FRAMING, false)) {
		nfc_perror(pnd, "nfc_configure");
		exit(EXIT_FAILURE);
	}
	// Disable 14443-4 autoswitching
	if (!nfc_configure(pnd, NDO_AUTO_ISO14443_4, false)) {
		nfc_perror(pnd, "nfc_configure");
		exit(EXIT_FAILURE);
	}
	// Force 14443-A mode
	if (!nfc_configure(pnd, NDO_FORCE_ISO14443_A, true)) {
		nfc_perror(pnd, "nfc_configure");
		exit(EXIT_FAILURE);
	}

	// Enable field so more power consuming cards can power themselves up
	if (!nfc_configure(pnd, NDO_ACTIVATE_FIELD, true)) {
		nfc_perror(pnd, "nfc_configure");
		exit(EXIT_FAILURE);
	}

	printf("Connected to NFC reader: %s\n\n", pnd->acName);

	// Send the 7 bits request command specified in ISO 14443A (0x26)
	if (!transmit_bits(abtReqa, 7)) {
		printf("Error: No tag available\n");
		nfc_disconnect(pnd);
		return;
	}
	memcpy(abtAtqa, abtRx, 2);

	memcpy(abtSelectTag+2, uid, 4);
	iso14443a_crc_append(abtSelectTag, 7);
	transmit_bytes(abtSelectTag, 9);

	memcpy(abtSelectTag+6, uid+4, 4);
	iso14443a_crc_append(abtSelectTag2, 7);
	transmit_bytes(abtSelectTag2, 9);

	iso14443a_crc_append(abtRats, 2);
	transmit_bytes(abtRats, 4);

	//iso14443a_crc_append(write, 5); // write seq
	//transmit_bytes(write, 7);
	//iso14443a_crc_append(write, 6); // write response
	//transmit_bytes(write, 8);

	nfc_disconnect(pnd);
	return;
}

char *base64_encode(const char *data,
                    size_t input_length,
                    size_t *output_length) {

    *output_length = (size_t) (4.0 * ceil((double) input_length / 3.0));

    char *encoded_data = malloc(*output_length);
    if (encoded_data == NULL) return NULL;
    int i,j;
    for (i = 0, j = 0; i < input_length;) {

        uint32_t octet_a = i < input_length ? data[i++] : 0;
        uint32_t octet_b = i < input_length ? data[i++] : 0;
        uint32_t octet_c = i < input_length ? data[i++] : 0;

        uint32_t triple = (octet_a << 0x10) + (octet_b << 0x08) + octet_c;

        encoded_data[j++] = encoding_table[(triple >> 3 * 6) & 0x3F];
        encoded_data[j++] = encoding_table[(triple >> 2 * 6) & 0x3F];
        encoded_data[j++] = encoding_table[(triple >> 1 * 6) & 0x3F];
        encoded_data[j++] = encoding_table[(triple >> 0 * 6) & 0x3F];
    }
    for (i = 0; i < mod_table[input_length % 3]; i++)
        encoded_data[*output_length - 1 - i] = '=';

    return encoded_data;
}


char *base64_decode(const char *data,
                    size_t input_length,
                    size_t *output_length) {

    if (decoding_table == NULL) build_decoding_table();

    if (input_length % 4 != 0) return NULL;

    *output_length = input_length / 4 * 3;
    if (data[input_length - 1] == '=') (*output_length)--;
    if (data[input_length - 2] == '=') (*output_length)--;

    char *decoded_data = malloc(*output_length);
    if (decoded_data == NULL) return NULL;
    int i,j;
    for (i = 0, j = 0; i < input_length;) {

        uint32_t sextet_a = data[i] == '=' ? 0 & i++ : decoding_table[data[i++]];
        uint32_t sextet_b = data[i] == '=' ? 0 & i++ : decoding_table[data[i++]];
        uint32_t sextet_c = data[i] == '=' ? 0 & i++ : decoding_table[data[i++]];
        uint32_t sextet_d = data[i] == '=' ? 0 & i++ : decoding_table[data[i++]];

        uint32_t triple = (sextet_a << 3 * 6)
                        + (sextet_b << 2 * 6)
                        + (sextet_c << 1 * 6)
                        + (sextet_d << 0 * 6);

        if (j < *output_length) decoded_data[j++] = (triple >> 2 * 8) & 0xFF;
        if (j < *output_length) decoded_data[j++] = (triple >> 1 * 8) & 0xFF;
        if (j < *output_length) decoded_data[j++] = (triple >> 0 * 8) & 0xFF;
    }

    return decoded_data;
}


void build_decoding_table() {

    decoding_table = malloc(256);
    int i;
    for (i = 0; i < 0x40; i++)
        decoding_table[encoding_table[i]] = i;
}


void base64_cleanup() {
    free(decoding_table);
}
