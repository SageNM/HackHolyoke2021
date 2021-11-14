#!/usr/bin/env python3

# It's commonly the case that you already *have* a sprite sheet with
# square sprites packed into some rectangular texture.  But libGDX
# doesn't know how to load such a sheet by itself -- it still needs
# a data file that contains info on where each sprite is.
# This program generates such sheet info, which you can then edit
# if you so desire.

import sys
import argparse
import cv2

def info (*args):
  sys.stderr.write(" ".join(str(x) for x in args))
  sys.stderr.write("\n")

def out (*args):
  sys.stdout.write(" ".join(str(x) for x in args))
  sys.stdout.write("\n")


parser = argparse.ArgumentParser("Create libGDX texture atlas info.")
parser.add_argument("file", type=str, nargs=1,
                    help="The image file holding the sprites.")
parser.add_argument("--sprites-wide", type=int,
                    help="The number of sprites in the sheet horizontally.")
parser.add_argument("--sprites-high", type=int,
                    help="The number of sprites in the sheet vertically.")
parser.add_argument("--sprite-width", type=int,
                    help="The width of each sprite.")
parser.add_argument("--sprite-height", type=int,
                    help="The height of each sprite.")
args = parser.parse_args()

img = cv2.imread(args.file[0])

width = len(img[0])
height = len(img)

def figure (img, num, size):
  if num is None and size is None:
    raise RuntimeError("You must pass size information")
  if num is not None and size is not None:
    raise RuntimeError("Only use one type of size info per dimension")

  if size is None: size = img // num

  if num is None:
    if img % size == 0:
      # Good!
      num = img // size
    elif img % size == (size-1):
      # Probably one pixel padding between things except last
      num = img // size + 1
    else:
      print(img,num,size)
      raise RuntimeError("Can't figure out sprite sizes")

  return (num,size)


xnum,xsize = figure(width, args.sprites_wide, args.sprite_width)
ynum,ysize = figure(height, args.sprites_high, args.sprite_height)

info("There are %sx%s sprites of size %sx%s" % (xnum,ynum, xsize,ysize))


out(args.file[0])
out("  size:%s, %s" % (width,height))
out("  format: RGBA8888") # We don't check this!
out("  filter: Linear, Linear")
out("  repeat: none")
out("  pma: false")

for yy in range(ynum):
  for xx in range(xnum):
    out("s%s_%s" % (xx,yy))
    out("  bounds: %s, %s, %s, %s" % (xx*xsize,yy*ysize, xsize,ysize))
