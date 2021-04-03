from os import listdir
from os.path import isfile, join
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
import pylab
import matplotlib.cm as cm

LABELS = { "NUM_SPAWN_DEFENDER": "Defender Spawns",
           "SPEED_WIN": "Win Turn",
           "ATTACKS" : "# Attacks",
           "PERC_RANGE" : "Support Unit (Range - Melee)",
           "PRODUCTION" : "Final Production",
           }

RANGES = { "NUM_SPAWN_DEFENDER": np.arange(0, 10 + 1, step=1),
           "SPEED_WIN": np.arange(0, 50 + 1, step=5),
           "ATTACKS": np.arange(0, 20 + 1, step=2),
           "PERC_RANGE": np.arange(-5, 5 + 1, step=1),
           "PRODUCTION" : np.arange(0, 30 + 1, step=3),
           }



N_GAMES = 25
REP = 20
NUM_TECHS = 24


def heatmap(data, row_labels, col_labels, ax=None, clim=[0,100],
            cbar_kw={}, cbarlabel="", **kwargs):
    """
    Create a heatmap from a numpy array and two lists of labels.

    Parameters
    ----------
    data
        A 2D numpy array of shape (N, M).
    row_labels
        A list or array of length N with the labels for the rows.
    col_labels
        A list or array of length M with the labels for the columns.
    ax
        A `matplotlib.axes.Axes` instance to which the heatmap is plotted.  If
        not provided, use current axes or create a new one.  Optional.
    cbar_kw
        A dictionary with arguments to `matplotlib.Figure.colorbar`.  Optional.
    cbarlabel
        The label for the colorbar.  Optional.
    **kwargs
        All other arguments are forwarded to `imshow`.
    """

    if not ax:
        ax = plt.gca()

    # Plot the heatmap
    im = ax.imshow(data, **kwargs)

    # Create colorbar
    cbar = ax.figure.colorbar(im, ax=ax, **cbar_kw)
    cbar.ax.set_ylabel(cbarlabel, rotation=-90, va="bottom")
    im.set_clim(clim[0], clim[1])

    # We want to show all ticks...
    ax.set_xticks(np.arange(data.shape[1]))
    ax.set_yticks(np.arange(data.shape[0]))
    # ... and label them with the respective list entries.
    ax.set_xticklabels(col_labels)
    ax.set_yticklabels(row_labels)

    # Let the horizontal axes labeling appear on top.
    ax.tick_params(top=False, bottom=True,
                   labeltop=False, labelbottom=True)

    # Rotate the tick labels and set their alignment.
    plt.setp(ax.get_xticklabels(), rotation=-30, ha="right",
             rotation_mode="anchor")

    # Turn spines off and create white grid.
    # ax.spines[:].set_visible(False)

    ax.set_xticks(np.arange(data.shape[1] + 1) - .5, minor=True)
    ax.set_yticks(np.arange(data.shape[0] + 1) - .5, minor=True)
    ax.grid(which="minor", color="w", linestyle='-', linewidth=3)
    ax.tick_params(which="minor", bottom=False, left=False)

    return im, cbar


def annotate_heatmap(im, data=None, valfmt="{x:.2f}",
                     emptytext="",
                     textcolors=("black", "white"),
                     threshold=None, **textkw):
    """
    A function to annotate a heatmap.

    Parameters
    ----------
    im
        The AxesImage to be labeled.
    data
        Data used to annotate.  If None, the image's data is used.  Optional.
    valfmt
        The format of the annotations inside the heatmap.  This should either
        use the string format method, e.g. "$ {x:.2f}", or be a
        `matplotlib.ticker.Formatter`.  Optional.
    textcolors
        A pair of colors.  The first is used for values below a threshold,
        the second for those above.  Optional.
    threshold
        Value in data units according to which the colors from textcolors are
        applied.  If None (the default) uses the middle of the colormap as
        separation.  Optional.
    **kwargs
        All other arguments are forwarded to each call to `text` used to create
        the text labels.
    """

    if not isinstance(data, (list, np.ndarray)):
        data = im.get_array()

    # Normalize the threshold to the images color range.
    if threshold is not None:
        threshold = im.norm(threshold)
    else:
        threshold = im.norm(data.max()) / 2.

    # Set default alignment to center, but allow it to be
    # overwritten by textkw.
    kw = dict(horizontalalignment="center",
              verticalalignment="center")
    kw.update(textkw)

    # Get the formatter in case a string is supplied
    if isinstance(valfmt, str):
        valfmt = ticker.StrMethodFormatter(valfmt)

    # Loop over the data and create a `Text` for each "pixel".
    # Change the text's color depending on the data.
    texts = []
    for i in range(data.shape[0]):
        for j in range(data.shape[1]):
            kw.update(color=textcolors[int(im.norm(data[i, j]) > threshold)])
            # texts.append(text)
            if data[i, j] > 0:
                text = im.axes.text(j, i, valfmt(data[i, j], None), **kw)
                texts.append(text)
            else:
                texts.append(emptytext)
    return texts


def extract_features(line):
    data = {}
    idxStart = str.index(line, ":")
    featuresStr = line[idxStart + 1:-1]
    features = str.strip(featuresStr).split(",")
    for feat in features:
        feat = str.strip(feat)
        if len(feat) > 0:
            fs = feat.split(":")
            data[fs[0]] = float(fs[1])
    return data


def process_file(f_name):
    data = {}
    with open(join(file_path, f_name)) as f:
        lines = f.readlines()
        w = lines[0].split(":")[1][2:-2]
        weights = []
        for w_i in str.split(w, ","):
            weights.append(w_i)
        data["weights"] = weights

        data["map features"] = extract_features(lines[1])
        data["no map features"] = extract_features(lines[2])
    return data


def getsubgrid(x_min, x_max, y_min, y_max, grid):
    return [item[x_min:x_max] for item in grid[y_min:y_max]]


def draw_heatmap(labels, data, output_filename, x_min, x_max, y_min, y_max):
    pylab.figure()
    mapelites_wins = np.array(getsubgrid(y_min, y_max+1, x_min, x_max+1, data))
    x_ticks = RANGES[labels[0]][y_min:y_max+1]
    y_ticks = RANGES[labels[1]][x_min:x_max+1]

    # mapelites_wins = np.array(data)
    # x_ticks = [ x for x in range(0, len(RANGES[labels[0]][y_min:y_max+1])) ]
    # y_ticks = [ x for x in range(0, len(RANGES[labels[1]][x_min:x_max+1])) ]

    fig, ax = plt.subplots()
    im, cbar = heatmap(mapelites_wins, y_ticks, x_ticks, ax=ax,
                       cmap="YlGn", cbarlabel="Win Rate (%)")
    annotate_heatmap(im, valfmt="{x:.0f}%")
    plt.xlabel(LABELS[labels[0]])
    plt.ylabel(LABELS[labels[1]])

    plt.show()
    outputFile = join(file_path, output_filename)
    fig.savefig(outputFile)


def twoD(output_filename = "heatmap.png"):
    x_min = y_min = 1000
    x_max = y_max = -1000
    N = 12
    labels = ["x", "y"]

    mapelites = [[0 for _ in range(N)] for _ in range(N)]
    mapelites_wins = [[0.0 for _ in range(N)] for _ in range(N)]
    mapelites_dict = [[None for _ in range(N)] for _ in range(N)]
    for f_name in listdir(file_path):
        if ".txt" in f_name:
            mapfile = f_name.split("-")
            y = int(mapfile[0])
            x = int(mapfile[1])

            mapelites[x][y] += 1
            data = process_file(f_name)
            mapelites_dict[x][y] = data
            mapelites_wins[x][y] = data["no map features"]["WIN"]

            keys = data['map features'].keys()
            i = 0
            for k in keys:
                labels[i] = k
                i += 1

            if x < x_min:
                x_min = x
            if x > x_max:
                x_max = x
            if y < y_min:
                y_min = y
            if y > y_max:
                y_max = y

    for f in mapelites:
        print(f)

    print(str(x_min) + "-" + str(x_max) + ", " +str(y_min) + "-" +str(y_max))

    draw_heatmap(labels, mapelites_wins, output_filename + ".png", x_min, x_max, y_min, y_max)


    #
    # pylab.figure()
    # mapelites_wins = np.array(getsubgrid(y_min, y_max+1, x_min, x_max+1, mapelites_wins))
    # x_ticks = RANGES[labels[0]][y_min:y_max+1]
    # y_ticks = RANGES[labels[1]][x_min:x_max+1]
    #
    # fig, ax = plt.subplots()
    # im, cbar = heatmap(mapelites_wins, y_ticks, x_ticks, ax=ax,
    #                    cmap="YlGn", cbarlabel="Win Rate (%)")
    # annotate_heatmap(im, valfmt="{x:.0f}%")
    # plt.xlabel(LABELS[labels[0]])
    # plt.ylabel(LABELS[labels[1]])
    #
    # plt.show()
    # outputFile = join(file_path, output_filename)
    # fig.savefig(outputFile)



def threeD(output_filename = "heatmap.png"):
    x_min = y_min = z_min = 1000
    x_max = y_max = z_max = -1000
    N = 12
    labels = ["x", "y", "z"]

    mapelites = [[[0 for _ in range(N)] for _ in range(N)] for _ in range(N)]
    mapelites_wins = [[[0.0 for _ in range(N)] for _ in range(N)] for _ in range(N)]
    mapelites_dict = [[[None for _ in range(N)] for _ in range(N)] for _ in range(N)]

    X_proj_wins = Y_proj_wins = Z_proj_wins = [[0.0 for _ in range(N)] for _ in range(N)]
    X_proj_dict = Y_proj_dict = Z_proj_dict = [[None for _ in range(N)] for _ in range(N)]


    for f_name in listdir(file_path):
        if ".txt" in f_name:
            mapfile = f_name.split("-")
            y = int(mapfile[0])
            x = int(mapfile[1])
            z = int(mapfile[2])

            mapelites[x][y][z] += 1
            data = process_file(f_name)
            mapelites_dict[x][y][z] = data
            win_pc = data["no map features"]["WIN"]
            mapelites_wins[x][y][z] = win_pc

            if win_pc > X_proj_wins[y][z]:
                X_proj_wins[y][z] = win_pc
                X_proj_dict[y][z] = data

            if win_pc > Y_proj_wins[x][z]:
                Y_proj_wins[x][z] = win_pc
                Y_proj_dict[x][z] = data

            if win_pc > Z_proj_wins[x][y]:
                Z_proj_wins[x][y] = win_pc
                Z_proj_dict[x][y] = data


            keys = data['map features'].keys()
            i = 0
            for k in keys:
                labels[i] = k
                i += 1

            if x < x_min:
                x_min = x
            if x > x_max:
                x_max = x
            if y < y_min:
                y_min = y
            if y > y_max:
                y_max = y
            if z < z_min:
                z_min = z
            if z > z_max:
                z_max = z

    for f in mapelites:
        print(f)

    print(str(x_min) + "-" + str(x_max) + ", " +str(y_min) + "-" +str(y_max) + ", " +str(z_min) + "-" +str(z_max))

    draw_heatmap([labels[1], labels[2]], X_proj_wins, output_filename + "_X.png", y_min, y_max, z_min, z_max)
    draw_heatmap([labels[0], labels[2]], Y_proj_wins, output_filename + "_Y.png", x_min, x_max, z_min, z_max)
    draw_heatmap([labels[0], labels[1]], Z_proj_wins, output_filename + "_Z.png", x_min, x_max, y_min, y_max)




file_path = "C:\\Work\\Tribes-results\\mcts-3-distributed-b\\map\\"
output_file = "heatmap"
# output_file = "attacks-support-hm"

def main():
    # twoD(file_path + "/" + output_file)
    threeD(file_path + "/" + output_file)


main()
