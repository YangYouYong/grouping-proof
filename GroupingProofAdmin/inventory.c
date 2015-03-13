#include <stdio.h>
#include <stdlib.h>

#include <curl/curl.h>
#include "cJSON.h"
#include "nfc.h"

typedef struct MemStore {
	char* str;
	size_t size;
} MemStore;

cJSON* getURL(char* url);
void addInventory();
void processOrder();
size_t function(char *ptr, size_t size, size_t nmemb, void *userdata);

int main(int argc, char *argv[]) {
	char c;
	do {
		printf("Please select mode:\n");
		printf("\t1 - Inventory Add\n");
		printf("\t2 - Process Order\n");
		printf("\t3 - Quit\n");

		c = getchar();
		getchar();
		switch (c) {
		case '1':
			addInventory();
			break;
		case '2':
			processOrder();
			break;
		case '3':
			return 0;
			break;
		default:
			printf("Invalid choice\n");
		}
	} while (1);

	return 0;
}

void addInventory() {
	cJSON* json =
			getURL("http://project..com/product/typelist.php");
	char c;
	do {
		printf("Please select product type:\n");
		cJSON* product = json->child->child;
		int i = 1;
		do {
			char* name = cJSON_GetObjectItem(product, "name")->valuestring;
			printf("\t%d - %s\n", i, name);
			i++;
		} while ((product = product->next) != 0);
		printf("\t%d - Return\n", i);

		c = getchar();
		getchar();
		if (c - 48 == i) {
			cJSON_Delete(json);
			return;
		}
		product = cJSON_GetArrayItem(json->child, c - 49);
		char* tid = cJSON_GetObjectItem(product, "producttypeid")->valuestring;
		printf("\tPlease scan tag\n");
		UID* uid = readUID();
		size_t len = 0;
		char* epc = base64_encode(uid->uid, uid->length, &len);
		char* key = "test";
		printf("\tAdding tag with id: %s\n", epc);
		size_t newsize = 0;
		newsize = strlen(
				"http://project..com/product/additem.php?e=&t=&k=");
		newsize += strlen(tid);
		newsize += strlen(epc);
		char* url = malloc(newsize + 1);
		sprintf(
				url,
				"http://project..com/product/additem.php?e=%s&t=%s&k=%s",
				epc, tid, key);
		getURL(url);
		free(url);
		free(uid);
		free(epc);
	} while (1);
}

void processOrder() {
	cJSON* json = getURL("http://project..com/group/orderlist.php");
	cJSON* product = json->child->child;
	do {
		size_t newsize = strlen(
				"http://project..com/group/orderinfo.php?o=");
		char* gid = cJSON_GetObjectItem(product, "groupid")->valuestring;
		printf("Building Order %s:\n", gid);
		newsize += strlen(gid);
		char* url = malloc(newsize + 1);
		sprintf(url,
				"http://project..com/group/orderinfo.php?o=%s", gid);
		cJSON* json2 = getURL(url);
		free(url);
		cJSON* productinner = json2->child->child;
		if (productinner == 0) {
			printf("Empty\n");
			continue;
		}
		do {
			char* epc = cJSON_GetObjectItem(productinner, "epc")->valuestring;
			build_decoding_table();
			int len = 0;
			char* base = base64_decode(epc, strlen(epc), &len);
			base64_cleanup();
			printf("\tPlease scan tag with id %s\n", epc);
			// scan tag

			newsize = strlen(
					"http://project..com/group/checkout.php?e=");
			newsize += strlen(epc);
			url = malloc(newsize + 1);
			sprintf(url,
					"http://project..com/group/checkout.php?e=%s",
					epc);
			getURL(url);
			free(url);
		} while ((productinner = productinner->next) != 0);
		printf("1 - Return\n");
		printf("2 - Next Order\n");
		cJSON_Delete(json2);
		char c;
		c = getchar();
		getchar();
		if (c == '1')
			;
		{
			cJSON_Delete(json);
			return;
		}
	} while ((product = product->next) != 0);
	cJSON_Delete(json);
}

cJSON* getURL(char* url) {
	cJSON* info = 0;
	MemStore str;
	CURL *curl;
	CURLcode res;
	str.size = 0;
	str.str = 0;
	curl = curl_easy_init();
	if (curl) {
		curl_easy_setopt(curl, CURLOPT_URL, url);
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, function);
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, &str);
		res = curl_easy_perform(curl);

		curl_easy_cleanup(curl);
	}
	info = cJSON_Parse(str.str);
	free(str.str);
	return info;
}

size_t function(char *ptr, size_t size, size_t nmemb, void *userdata) { //make safe
	MemStore* mem = (MemStore*) userdata;
	size_t prevsize = mem->size;
	mem->size += size * nmemb;
	char* tmp = malloc(mem->size);
	if (prevsize > 0) {
		memcpy(tmp, mem->str, prevsize);
	}
	memcpy(tmp + prevsize, ptr, size * nmemb);
	if (mem->str != 0) {
		free(mem->str);
	}
	mem->str = tmp;
	return size * nmemb;
}
