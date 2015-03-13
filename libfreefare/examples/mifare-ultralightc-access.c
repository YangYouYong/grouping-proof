/*-
 * Copyright (C) 2010, Romain Tartiere.
 * 
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * 
 * $Id: mifare-desfire-access.c 646 2010-10-29 13:01:55Z rtartiere@il4p.fr $
 */

#include <err.h>
#include <errno.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>

#include <nfc/nfc.h>
#include <openssl/des.h>

#include <freefare.h>

uint8_t key_data_null[16]  = { 0x49, 0x45, 0x4D, 0x4B, 0x41, 0x45, 0x52, 0x42, 0x21, 0x4E, 0x41, 0x43, 0x55, 0x4F, 0x59, 0x46 };//{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

int
main(int argc, char *argv[])
{
	    MifareDESFireKey key2 = mifare_desfire_3des_key_new_with_version (key_data_null);
	    mifare_ultralightc_authenticate_test (key2);
    int error = EXIT_SUCCESS;
    nfc_device_t *device = NULL;
    MifareTag *tags = NULL;

    if (argc > 1)
	errx (EXIT_FAILURE, "usage: %s", argv[0]);

    nfc_device_desc_t devices[8];
    size_t device_count;

    nfc_list_devices (devices, 8, &device_count);
    if (!device_count)
	errx (EXIT_FAILURE, "No NFC device found.");

    for (size_t d = 0; d < device_count; d++) {
	device = nfc_connect (&(devices[d]));
	if (!device) {
	    warnx ("nfc_connect() failed.");
	    error = EXIT_FAILURE;
	    continue;
	}

	tags = freefare_get_tags (device);
	if (!tags) {
	    nfc_disconnect (device);
	    errx (EXIT_FAILURE, "Error listing tags.");
	}

	for (int i = 0; (!error) && tags[i]; i++) {
	    if (ULTRALIGHT_C != freefare_get_tag_type (tags[i]))
		continue;

	    int res;
	    char *tag_uid = freefare_get_tag_uid (tags[i]);

	    res = mifare_ultralight_connect (tags[i]);
	    if (res < 0) {
		warnx ("Can't connect to Mifare Ultralight c target.");
		error = EXIT_FAILURE;
		break;
	    }

	    MifareDESFireKey key = mifare_desfire_3des_key_new_with_version (key_data_null);
	    res = mifare_ultralightc_authenticate (tags[i], key);

	    if (res < 0)
	    {
		errx (EXIT_FAILURE, "Ultralight C Authentication failed");
            } else {
		warnx ("Ultralight C Authenticated");
	    }

	    mifare_desfire_key_free (key);
	    free (tag_uid);

	    mifare_ultralight_disconnect (tags[i]);
	}

	freefare_free_tags (tags);
	nfc_disconnect (device);
    }
    
    exit (error);
} /* main() */

