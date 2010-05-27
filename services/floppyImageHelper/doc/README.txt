Debian
------

On Debian, you need 'dd', which should be included with the core OS.

You also need 'pmount', so 

  aptitude install pmount

do "sudo modprobe loop" to get /dev/loop[1-9]. I recommend to add loop to /etc/modules to load it automatically at boot-time.

Also be sure that the /etc/pmount.allow looks like:

# /etc/pmount.allow
# pmount will allow users to additionally mount all devices that are # listed here.
/dev/loop[01234567]

finally check if the jboss-user is member of disk and plugdev group.
