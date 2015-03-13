# $Id: CMakeLists.txt 789 2011-04-24 10:21:03Z rtartiere@il4p.fr $
set(EXAMPLES-SOURCES
        mifare-classic-format
        mifare-classic-write-ndef
	mifare-ultralightc-access
        mifare-desfire-access
        mifare-desfire-format
        mifare-desfire-info
        mifare-desfire-write-ndef
        mifare-desfire-ev1-configure-ats
        mifare-desfire-ev1-configure-default-key
        mifare-desfire-ev1-configure-random-uid
)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../libfreefare)

# Examples
foreach(source ${EXAMPLES-SOURCES})
  add_executable(${source} ${source}.c)
  target_link_libraries(${source} freefare)
  install(TARGETS ${source} RUNTIME DESTINATION bin COMPONENT examples)
endforeach(source)
