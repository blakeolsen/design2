import bluetooth

discovered_devices = bluetooth.discovered_devices(lookup_names=True)
print("found %d devices" % len(discovered_devices))

for add, name in discovered_devices:
	print("  %s - %s" % (addr, name))